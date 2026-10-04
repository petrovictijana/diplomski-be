package com.tijana.petrovic.diplomski_be.identity.exception;

public class InactiveUserAlreadyExistsException extends RuntimeException {

  public InactiveUserAlreadyExistsException(String message) {
        super(message);
    }
}
