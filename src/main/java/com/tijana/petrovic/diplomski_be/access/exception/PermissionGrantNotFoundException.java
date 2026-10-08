package com.tijana.petrovic.diplomski_be.access.exception;

/** Raised when revoking a grant that does not exist or is already revoked. */
public class PermissionGrantNotFoundException extends RuntimeException {

    public PermissionGrantNotFoundException(String message) {
        super(message);
    }
}
