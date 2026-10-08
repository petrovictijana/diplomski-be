package com.tijana.petrovic.diplomski_be.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tijana.petrovic.diplomski_be.document.service.DocumentService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Turns an S3 ObjectCreated notification into a confirmed upload.
 * <p>
 * This is the only moment the backend learns that the bytes behind a document actually
 * exist - the client uploads straight to S3 through a presigned URL, so nothing else
 * observes the upload. Parsing the event is an infrastructure concern and stays here; the
 * decision of what a confirmed upload means lives in {@link DocumentService}.
 */
@Log4j2
@RequiredArgsConstructor
@Component
public class DocumentUploadListener {

    private final DocumentService documentService;
    private final ObjectMapper objectMapper;

    @SqsListener("${aws.sqs.document-upload-queue}")
    public void onUpload(String payload) {
        var event = parse(payload);

        if (event == null || event.records() == null || event.records().isEmpty()) {
            log.debug("[DocumentUploadListener] Ignoring message without S3 records: {}", payload);
            return;
        }

        for (var record : event.records()) {
            var bucket = record.s3().bucket().name();
            // S3 URL-encodes the key in the notification (spaces as '+', others as %XX).
            var key = URLDecoder.decode(record.s3().object().key(), StandardCharsets.UTF_8);

            log.info("[DocumentUploadListener] Upload event for {}/{}", bucket, key);
            documentService.confirmUpload(bucket, key, record.s3().object().size());
        }
    }

    private S3EventPayload parse(String payload) {
        try {
            return objectMapper.readValue(payload, S3EventPayload.class);
        } catch (JsonProcessingException e) {
            log.warn("[DocumentUploadListener] Could not parse S3 event, ignoring: {}", payload);
            return null;
        }
    }

}
