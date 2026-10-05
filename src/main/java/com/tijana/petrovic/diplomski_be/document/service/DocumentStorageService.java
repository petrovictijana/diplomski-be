package com.tijana.petrovic.diplomski_be.document.service;

import com.tijana.petrovic.diplomski_be.aws.S3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.http.ContentDisposition;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * The document domain's view of object storage: it hands out storage keys and signs URLs,
 * and is the only place that knows the bucket layout.
 * <p>
 * Content never passes through the application - the client uploads to and downloads from
 * S3 directly - so the backend stays out of the way of large files and only has to decide
 * <em>whether</em> a URL may be issued.
 */
@Log4j2
@RequiredArgsConstructor
@Service
public class DocumentStorageService {

    private final S3Presigner s3Presigner;
    private final S3Config s3Config;

    /**
     * A fresh, unguessable key. Independent of both the filename and the document id: the
     * filename is user input and the id is handed to clients, and neither should let anyone
     * construct the key of a document they cannot see.
     */
    public String generateStorageKey(UUID documentId) {
        return s3Config.getKeyPrefix() + documentId;
    }

    /**
     * Signs a single {@code PUT} of exactly this key with exactly this content type. Both
     * are part of the signature, so the URL cannot be reused to overwrite another object or
     * to upload content of a different type than the one recorded in the metadata.
     */
    public PresignedUpload presignUpload(String storageKey) {
        var putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Config.getBucket())
                .key(storageKey)
                .build();

        var presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(s3Config.getPresignedUrlExpiration())
                .putObjectRequest(putObjectRequest)
                .build();

        var presigned = s3Presigner.presignPutObject(presignRequest);

        log.debug("[DocumentStorageService] Presigned upload for key {}", storageKey);

        return new PresignedUpload(
                presigned.url().toExternalForm(),
                presigned.expiration().atOffset(ZoneOffset.UTC)
        );
    }

    /**
     * Signs a single {@code GET} of exactly this key. The filename and content type are
     * signed in as response overrides, so S3 itself serves the file under the name it was
     * uploaded with - the storage key is a UUID and would otherwise be what the browser saves.
     * <p>
     * Whether the caller may read this document is decided before this method is reached:
     * once the URL exists, it grants access to anyone holding it until it expires.
     */
    public PresignedDownload presignDownload(String storageKey, String filename, String contentType) {
        var getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Config.getBucket())
                .key(storageKey)
                .responseContentDisposition(contentDisposition(filename))
                .responseContentType(contentType)
                .build();

        var presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(s3Config.getPresignedUrlExpiration())
                .getObjectRequest(getObjectRequest)
                .build();

        var presigned = s3Presigner.presignGetObject(presignRequest);

        log.debug("[DocumentStorageService] Presigned download for key {}", storageKey);

        return new PresignedDownload(
                presigned.url().toExternalForm(),
                presigned.expiration().atOffset(ZoneOffset.UTC)
        );
    }

    /**
     * Built through Spring's {@link ContentDisposition} rather than by hand: it quotes and
     * RFC 5987 encodes the value, which a document named {@code "izvestaj; v2".pdf} would
     * otherwise break out of - and the header ends up in a signed URL, where a malformed
     * one cannot be fixed after the fact.
     */
    private String contentDisposition(String filename) {
        if (filename == null || filename.isBlank()) {
            return null;
        }

        return ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build()
                .toString();
    }

}
