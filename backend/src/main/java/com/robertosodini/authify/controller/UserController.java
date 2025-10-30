package com.robertosodini.authify.controller;

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

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody @Valid UserRequestDto request){
        return ResponseEntity.status(201).body(userService.createUser(request));
        // TODO: send email
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponseDto> getUserInfo(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return ResponseEntity.ok(userService.getUserInfo(email));
    }
}
