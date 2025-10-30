package com.robertosodini.authify.exceptions;

public class EmailAlredyRegister extends RuntimeException {
    public EmailAlredyRegister(String message) {
        super(message);
    }
}
