package com.robertosodini.authify.controller;

import com.robertosodini.authify.dto.PasswordRequestDto;
import com.robertosodini.authify.dto.UserRequestDto;
import com.robertosodini.authify.dto.UserResponseDto;
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
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody @Valid UserRequestDto request){
        return ResponseEntity.status(201).body(userService.createUser(request));
    }

    ///  Profilo utente
    @GetMapping("/user")
    public ResponseEntity<UserResponseDto> getUserInfo(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return ResponseEntity.ok(userService.getUserInfo(email));
    }

    ///  Password reset OTP
    @PostMapping("/send-reset-otp")
    public ResponseEntity<String> sendResetOtp(@RequestParam String email){
        return ResponseEntity.ok(userService.sendResetOtp(email));
    }

    /// Salva nuova password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid PasswordRequestDto request){
        return ResponseEntity.ok(userService.resetPassword(request));
    }


}
