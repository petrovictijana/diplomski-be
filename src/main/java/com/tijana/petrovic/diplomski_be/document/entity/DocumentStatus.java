package com.tijana.petrovic.diplomski_be.document.entity;

/**
 * Lifecycle of a document row relative to its file content.
 * <p>
 * The row is created before the bytes exist, because the client uploads them straight to
 * object storage through a presigned URL. Until the upload event arrives the document is
 * a reservation: it has an identity and a storage key, but no content behind it.
 */
public enum DocumentStatus {

    /**
     * Metadata row exists and a presigned URL has been handed out, but the bytes have not
     * been confirmed. Such a document must stay out of listings, search results and downloads.
     */
    PENDING,

    /** The upload was confirmed, so the size and checksum are known and the content is readable. */
    UPLOADED
}
