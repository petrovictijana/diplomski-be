package com.tijana.petrovic.diplomski_be.document.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Carries its own status so the endpoint answers 400 before the global exception handler
 * exists. A {@code @ControllerAdvice} handler takes precedence over this annotation, so
 * nothing here has to change once that is in place.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnknownLabelException extends RuntimeException {

    public UnknownLabelException(String message) {
        super(message);
    }
}
