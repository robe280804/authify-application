package com.robertosodini.authify.exceptions;

public class RateLimitExceed extends RuntimeException {
    public RateLimitExceed(String message) {
        super(message);
    }
}
