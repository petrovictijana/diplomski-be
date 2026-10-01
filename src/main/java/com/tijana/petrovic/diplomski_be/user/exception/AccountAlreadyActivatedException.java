package com.tijana.petrovic.diplomski_be.user.exception;

public class AccountAlreadyActivatedException extends RuntimeException {
    public AccountAlreadyActivatedException(String message) {
        super(message);
    }
}
