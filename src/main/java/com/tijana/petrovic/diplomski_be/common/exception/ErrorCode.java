package com.tijana.petrovic.diplomski_be.common.exception;

import org.springframework.http.HttpStatus;

/**
 * The single catalogue of machine-readable error codes returned to the client as
 * {@code { code, message }}. Each code owns the HTTP status it maps to, so the status
 * lives in one place and the {@link GlobalExceptionHandler} only has to translate an
 * exception type into a code.
 */
public enum ErrorCode {

    // identity
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    USER_ALREADY_ACTIVE(HttpStatus.CONFLICT),
    USER_INVITATION_PENDING(HttpStatus.CONFLICT),
    ACCOUNT_ALREADY_ACTIVATED(HttpStatus.CONFLICT),
    ACCOUNT_NOT_ACTIVATED(HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED),
    INVALID_VERIFICATION_TOKEN(HttpStatus.BAD_REQUEST),
    VERIFICATION_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST),
    NO_AUTHENTICATED_USER(HttpStatus.UNAUTHORIZED),

    // document
    DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND),
    DOCUMENT_ALREADY_UPLOADED(HttpStatus.CONFLICT),
    DOCUMENT_CONTENT_NOT_AVAILABLE(HttpStatus.CONFLICT),
    LABEL_ALREADY_EXISTS(HttpStatus.CONFLICT),
    UNKNOWN_LABEL(HttpStatus.BAD_REQUEST),

    // request shape
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST),
    MALFORMED_REQUEST(HttpStatus.BAD_REQUEST),

    // fallback for anything not explicitly mapped
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
