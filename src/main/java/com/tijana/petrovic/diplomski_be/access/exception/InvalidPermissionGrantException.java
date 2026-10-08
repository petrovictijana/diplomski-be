package com.tijana.petrovic.diplomski_be.access.exception;

/**
 * Raised when a grant request is malformed: no single subject, an action that does not apply
 * to the resource type, a SYSTEM grant carrying a resource, or a subject/resource that does
 * not exist.
 */
public class InvalidPermissionGrantException extends RuntimeException {

    public InvalidPermissionGrantException(String message) {
        super(message);
    }
}
