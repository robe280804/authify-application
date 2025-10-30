package com.robertosodini.authify.service;

import com.robertosodini.authify.dto.AuthRequestDto;
import com.robertosodini.authify.dto.AuthResponseDto;
import com.robertosodini.authify.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthResponseDto login(@Valid AuthRequestDto request) {
        Authentication auth = authenticate(request.getEmail(), request.getPassword());
        final UserDetails userDetails = (UserDetails) auth.getPrincipal();
        final String token = jwtUtil.generateToken(userDetails);

        return new AuthResponseDto(userDetails.getUsername(), token);
    }

    private Authentication authenticate(String email, String password) {
        Authentication auth;

        try {
            auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            return auth;

        } catch(BadCredentialsException ex) {
            throw new BadCredentialsException("Email o password errati");

        } catch(DisabledException ex) {
            throw new DisabledException("Account disabilitato");

        } catch(Exception ex) {
            throw new RuntimeException("Autenticazione fallita");
        }
    }
}
