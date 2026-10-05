package com.tijana.petrovic.diplomski_be.document.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Carries its own status so the endpoint answers 404 before the global exception handler
 * exists. A {@code @ControllerAdvice} handler takes precedence over this annotation, so
 * nothing here has to change once that is in place.
 * <p>
 * Also the answer for a soft-deleted document, and - once access control is in place - for
 * one the caller is not allowed to see: "exists but forbidden" and "does not exist" have to
 * be indistinguishable, or the status code itself becomes a way to enumerate documents.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(String message) {
        super(message);
    }
}
