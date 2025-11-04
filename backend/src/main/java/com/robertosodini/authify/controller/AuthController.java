package com.robertosodini.authify.controller;

import com.robertosodini.authify.dto.AuthRequestDto;
import com.robertosodini.authify.dto.AuthResponseDto;
import com.robertosodini.authify.dto.OtpDto;
import com.robertosodini.authify.security.ratelimiter.RateLimit;
import com.robertosodini.authify.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /// Login
    @RateLimit(limit = 3, timesWindowSecond = 60)
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid AuthRequestDto request, HttpServletRequest httpRequest){
        AuthResponseDto response = authService.login(request, httpRequest);

        ResponseCookie cookie = ResponseCookie.from("jwt", response.getToken())
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AuthResponseDto(response.getEmail(), response.getToken()));
    }

    ///  Utente autenticato?
    @GetMapping("/is-authenticated")
    @RateLimit(limit = 10, timesWindowSecond = 60)
    public ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return ResponseEntity.ok(email != null);
    }

    /// OTP verifica account
    @RateLimit(limit = 3, timesWindowSecond = 60)
    @PostMapping("/send-otp")
    public ResponseEntity<Void> sendVerifyOtp(@CurrentSecurityContext(expression = "authentication?.name") String email){
        authService.sendOtp(email);
        return ResponseEntity.ok().build();
    }

    /// Conferma verifica account
    @RateLimit(limit = 10, timesWindowSecond = 60)
    @PostMapping("/verify-otp")
    public ResponseEntity<Void> verifyOtp(@RequestBody @Valid OtpDto request,
                                          @CurrentSecurityContext(expression = "authentication?.name") String email){
        authService.verifyOtp(request, email);
        return ResponseEntity.ok().build();
    }

    @RateLimit(limit = 10, timesWindowSecond = 60)
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CurrentSecurityContext(expression = "authentication?.name") String email, HttpServletResponse response){
        return ResponseEntity.ok(authService.logout(email, response));
    }
}
