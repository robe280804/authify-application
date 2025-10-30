package com.robertosodini.authify.controller;

import com.robertosodini.authify.dto.AuthRequestDto;
import com.robertosodini.authify.dto.AuthResponseDto;
import com.robertosodini.authify.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid AuthRequestDto request){
        AuthResponseDto response = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("jwt", response.getToken())
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponseDto(response.getEmail(), response.getToken()));
    }
}
