package com.tijana.petrovic.diplomski_be.identity.exception;

public class ActiveUserAlreadyExistsException extends RuntimeException {

    public ActiveUserAlreadyExistsException(String message) {
        super(message);
    }
}
