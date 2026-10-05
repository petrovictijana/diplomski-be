package com.tijana.petrovic.diplomski_be.document.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The document row exists but its content does not: the upload was never completed, so the
 * document is still {@code PENDING}. Deliberately not a 404 - the document is visible to its
 * owner and the distinction is what tells them the upload has to be retried.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DocumentContentNotAvailableException extends RuntimeException {

    public DocumentContentNotAvailableException(String message) {
        super(message);
    }
}
