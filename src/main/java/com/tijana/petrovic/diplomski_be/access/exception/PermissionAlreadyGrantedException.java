package com.tijana.petrovic.diplomski_be.access.exception;

/** Raised when the same right is already granted to the subject and still active. */
public class PermissionAlreadyGrantedException extends RuntimeException {

    public PermissionAlreadyGrantedException(String message) {
        super(message);
    }
}
