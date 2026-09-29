package com.tijana.petrovic.diplomski_be.user.exception;

public class InactiveUserAlreadyExistsException extends RuntimeException {

  public InactiveUserAlreadyExistsException(String message) {
        super(message);
    }
}
