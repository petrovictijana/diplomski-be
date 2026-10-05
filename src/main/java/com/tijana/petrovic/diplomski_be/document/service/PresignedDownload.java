package com.tijana.petrovic.diplomski_be.document.service;

import java.time.OffsetDateTime;

/**
 * A presigned GET the client downloads the document content from.
 * <p>
 * Kept separate from {@link PresignedUpload} because a download carries no header contract:
 * the filename and content type are signed into the URL and returned by S3 itself.
 */
public record PresignedDownload(
        String url,
        OffsetDateTime expiresAt
) {
}
