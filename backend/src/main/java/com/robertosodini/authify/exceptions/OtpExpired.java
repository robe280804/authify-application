package com.robertosodini.authify.exceptions;

public class OtpExpired extends RuntimeException {
    public OtpExpired(String message) {
        super(message);
    }
}
