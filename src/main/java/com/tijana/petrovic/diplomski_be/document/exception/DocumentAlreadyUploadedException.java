package com.tijana.petrovic.diplomski_be.document.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An upload URL was requested for a document whose content already exists. Refused rather
 * than signed: the key is fixed per document, so a second {@code PUT} would overwrite the
 * stored content while the recorded size and checksum keep describing the old bytes.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DocumentAlreadyUploadedException extends RuntimeException {

    public DocumentAlreadyUploadedException(String message) {
        super(message);
    }
}
