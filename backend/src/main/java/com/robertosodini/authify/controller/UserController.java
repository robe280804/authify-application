package com.robertosodini.authify.controller;

import com.robertosodini.authify.dto.PasswordRequestDto;
import com.robertosodini.authify.dto.UserRequestDto;
import com.robertosodini.authify.dto.UserResponseDto;
import com.robertosodini.authify.security.ratelimiter.RateLimit;
import com.robertosodini.authify.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    ///  Registrazione
    @RateLimit(limit = 3, timesWindowSecond = 60)
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody @Valid UserRequestDto request){
        return ResponseEntity.status(201).body(userService.createUser(request));
    }

    ///  Profilo utente
    @RateLimit(limit = 5, timesWindowSecond = 60)
    @GetMapping("/user")
    public ResponseEntity<UserResponseDto> getUserInfo(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return ResponseEntity.ok(userService.getUserInfo(email));
    }

    ///  Password reset OTP
    @RateLimit(limit = 3, timesWindowSecond = 60)
    @PostMapping("/send-reset-otp")
    public ResponseEntity<String> sendResetOtp(@RequestParam String email){
        return ResponseEntity.ok(userService.sendResetOtp(email));
    }

    /// Salva nuova password
    @RateLimit(limit = 3, timesWindowSecond = 60)
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid PasswordRequestDto request){
        return ResponseEntity.ok(userService.resetPassword(request));
    }


}
