package com.tijana.petrovic.diplomski_be.access.exception;

/**
 * Raised when a user lacks the right required for an operation.
 * <p>
 * Named distinctly from Spring Security's {@code AccessDeniedException} to avoid confusion:
 * this is the single domain-level signal, thrown only from {@code AccessControlService}.
 */
public class PermissionDeniedException extends RuntimeException {

    public PermissionDeniedException(String message) {
        super(message);
    }
}
