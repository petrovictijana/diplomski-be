#!/bin/bash

REGION="${AWS_DEFAULT_REGION:-eu-central-1}"
BUCKET="diplomski-bucket"
QUEUE="document-upload-events"
DLQ="${QUEUE}-dlq"

KEY_PREFIX="documents/"

# Both SQS RedrivePolicy and Policy are JSON documents carried inside a JSON string
# value, so they have to be escaped before being handed to the CLI.
as_json_string() {
    sed 's/\\/\\\\/g; s/"/\\"/g' | tr -d '\n'
}

echo "==> Creating dead letter queue: ${DLQ}"
awslocal sqs create-queue \
    --queue-name "${DLQ}"

DLQ_URL="$(awslocal sqs get-queue-url --queue-name "${DLQ}" --query 'QueueUrl' --output text)"
DLQ_ARN="$(awslocal sqs get-queue-attributes \
    --queue-url "${DLQ_URL}" \
    --attribute-names QueueArn \
    --query 'Attributes.QueueArn' --output text)"

echo "==> Creating queue: ${QUEUE}"
printf '{"deadLetterTargetArn":"%s","maxReceiveCount":"5"}' "${DLQ_ARN}" > /tmp/redrive.json
REDRIVE="$(as_json_string < /tmp/redrive.json)"

cat > /tmp/queue-attributes.json <<JSON
{
  "VisibilityTimeout": "60",
  "ReceiveMessageWaitTimeSeconds": "20",
  "MessageRetentionPeriod": "345600",
  "RedrivePolicy": "${REDRIVE}"
}
JSON

awslocal sqs create-queue \
    --queue-name "${QUEUE}" \
    --attributes file:///tmp/queue-attributes.json \
    > /dev/null

QUEUE_URL="$(awslocal sqs get-queue-url --queue-name "${QUEUE}" --query 'QueueUrl' --output text)"
QUEUE_ARN="$(awslocal sqs get-queue-attributes \
    --queue-url "${QUEUE_URL}" \
    --attribute-names QueueArn \
    --query 'Attributes.QueueArn' --output text)"

# LocalStack does not enforce this policy, but real S3 refuses to publish to a queue
# that has not granted it SendMessage - keeping it here makes the setup portable.
echo "==> Allowing S3 to publish to ${QUEUE}"
cat > /tmp/queue-policy.json <<JSON
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AllowS3BucketNotification",
      "Effect": "Allow",
      "Principal": { "Service": "s3.amazonaws.com" },
      "Action": "sqs:SendMessage",
      "Resource": "${QUEUE_ARN}",
      "Condition": {
        "ArnLike": { "aws:SourceArn": "arn:aws:s3:::${BUCKET}" }
      }
    }
  ]
}
JSON
POLICY="$(as_json_string < /tmp/queue-policy.json)"

cat > /tmp/queue-policy-attribute.json <<JSON
{ "Policy": "${POLICY}" }
JSON

awslocal sqs set-queue-attributes \
    --queue-url "${QUEUE_URL}" \
    --attributes file:///tmp/queue-policy-attribute.json

echo "==> Creating bucket: ${BUCKET}"
if ! awslocal s3api head-bucket --bucket "${BUCKET}" 2>/dev/null; then
    awslocal s3api create-bucket \
        --bucket "${BUCKET}" \
        --create-bucket-configuration "LocationConstraint=${REGION}" \
        > /dev/null
fi

# The browser PUTs straight to the presigned URL, so the bucket itself has to answer
# the CORS preflight. Dev origins only - a deployed bucket lists the real frontend host.
echo "==> Configuring bucket CORS"
cat > /tmp/bucket-cors.json <<'JSON'
{
  "CORSRules": [
    {
      "AllowedOrigins": [ "http://localhost:5173", "http://localhost:3000" ],
      "AllowedMethods": [ "PUT", "GET", "HEAD" ],
      "AllowedHeaders": [ "*" ],
      "ExposeHeaders": [ "ETag", "x-amz-checksum-sha256" ],
      "MaxAgeSeconds": 3000
    }
  ]
}
JSON
awslocal s3api put-bucket-cors --bucket "${BUCKET}" --cors-configuration file:///tmp/bucket-cors.json

# s3:ObjectCreated:* covers both a plain presigned PUT and a completed multipart upload.
echo "==> Wiring ${BUCKET}/${KEY_PREFIX} ObjectCreated -> ${QUEUE}"
cat > /tmp/bucket-notification.json <<JSON
{
  "QueueConfigurations": [
    {
      "Id": "document-uploaded",
      "QueueArn": "${QUEUE_ARN}",
      "Events": [ "s3:ObjectCreated:*" ],
      "Filter": {
        "Key": {
          "FilterRules": [ { "Name": "prefix", "Value": "${KEY_PREFIX}" } ]
        }
      }
    }
  ]
}
JSON

awslocal s3api put-bucket-notification-configuration \
    --bucket "${BUCKET}" \
    --notification-configuration file:///tmp/bucket-notification.json

echo
echo "AWS bootstrap complete"
echo "  endpoint   http://localhost:4566"
echo "  region     ${REGION}"
echo "  bucket     ${BUCKET}"
echo "  queue      ${QUEUE_ARN}"
echo "  dlq        ${DLQ_ARN}"
