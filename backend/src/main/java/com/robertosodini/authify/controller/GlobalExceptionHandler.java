package com.robertosodini.authify.controller;

import com.robertosodini.authify.exceptions.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VerificationUpdate.class)
    public ResponseEntity<Object> verificationUpdate(VerificationUpdate ex, WebRequest request){
        return generateResponse("VERIFICATION_UPDATE_ERR", HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(PasswordUpdate.class)
    public ResponseEntity<Object> passwordUpdate(PasswordUpdate ex, WebRequest request){
        return generateResponse("PASSWORD_UPDATE_ERR", HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(InvalidOtp.class)
    public ResponseEntity<Object> invalidOtp(InvalidOtp ex, WebRequest request){
        return generateResponse("INVALID_OTP", HttpStatus.UNAUTHORIZED, ex, request);
    }

    @ExceptionHandler(OtpExpired.class)
    public ResponseEntity<Object> otpExpired(OtpExpired ex, WebRequest request){
        return generateResponse("OTP_EXPIRED", HttpStatus.UNAUTHORIZED, ex, request);
    }

    @ExceptionHandler(EmailAlredyRegister.class)
    public ResponseEntity<Object> emailAlredyRegistered(EmailAlredyRegister ex, WebRequest request){
        return generateResponse("EMAIL_ALREDY_REGISTERED", HttpStatus.CONFLICT, ex, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> badCredential(BadCredentialsException ex, WebRequest request){
        return generateResponse("BAD_CREDENTIAL", HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Object> disable(DisabledException ex, WebRequest request){
        return generateResponse("ACCOUNT_DISABLE", HttpStatus.UNAUTHORIZED, ex, request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> illegalStateException(IllegalStateException ex, WebRequest request){
        return generateResponse("ILLEGAL_STATE", HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> usernameNotFound(UsernameNotFoundException ex, WebRequest request){
        return generateResponse("USERNAME_NOT_FOUND", HttpStatus.NOT_FOUND, ex, request);
    }

    private static ResponseEntity<Object> generateResponse(
            String error, HttpStatus status, Exception ex, WebRequest request
    ){
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status);
        response.put("error", error);
        response.put("message", ex.getMessage());
        response.put("path", extractPath(request));

        return new ResponseEntity<>(response, status);
    }

    private static String extractPath(WebRequest request){
        return request.getDescription(false).replace("uri=", "");
    }
}
