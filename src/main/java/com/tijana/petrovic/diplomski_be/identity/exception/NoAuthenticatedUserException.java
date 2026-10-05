package com.tijana.petrovic.diplomski_be.identity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a service needs the caller's identity but the security context holds none -
 * either the endpoint was left out of the authenticated matchers, or the token's user was
 * deleted after the token was issued.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NoAuthenticatedUserException extends RuntimeException {

    public NoAuthenticatedUserException(String message) {
        super(message);
    }
}
