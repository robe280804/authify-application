package com.robertosodini.authify.exceptions;

public class PasswordUpdate extends RuntimeException {
    public PasswordUpdate(String message) {
        super(message);
    }
}
