package com.tijana.petrovic.diplomski_be.user.exception;

public class ActiveUserAlreadyExistsException extends RuntimeException {

    public ActiveUserAlreadyExistsException(String message) {
        super(message);
    }
}
