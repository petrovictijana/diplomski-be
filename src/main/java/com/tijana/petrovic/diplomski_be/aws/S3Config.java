package com.tijana.petrovic.diplomski_be.aws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws.s3")
public class S3Config {

    private String bucket;

    /** Every document key is generated under this prefix - the bucket notification filters on it. */
    private String keyPrefix = "documents/";

    /**
     * Lifetime of a presigned URL. Long enough to upload or download a file, short enough
     * that a leaked URL stops working quickly.
     */
    private Duration presignedUrlExpiration = Duration.ofMinutes(15);

    /**
     * Puts the bucket in the path instead of the host. Required against LocalStack, because
     * virtual-hosted style would resolve to {@code bucket.localhost}.
     */
    private boolean pathStyleAccess;

}
