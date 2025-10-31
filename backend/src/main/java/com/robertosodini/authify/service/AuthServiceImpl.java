package com.robertosodini.authify.service;

import com.robertosodini.authify.dto.AuthRequestDto;
import com.robertosodini.authify.dto.AuthResponseDto;
import com.robertosodini.authify.dto.OtpDto;
import com.robertosodini.authify.exceptions.InvalidOtp;
import com.robertosodini.authify.exceptions.OtpExpired;
import com.robertosodini.authify.model.UserModel;
import com.robertosodini.authify.repository.UserRepository;
import com.robertosodini.authify.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
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

    @Override
    public void sendOtp(String email) {
        UserModel existUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));

        if (existUser.getIsAccountVerify() != null && existUser.getIsAccountVerify()){
            return;
        }
        // Genero otp a 6 cifre
        String otp =  String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        // Expire time 24 ore             
        long expiration = System.currentTimeMillis() + (24 * 60  * 60 * 1000);

        // TODO: Eseguire in un query sola dopo per evitare N+1
        existUser.setVerifyOtp(otp);
        existUser.setVerifyOtpExpireAt(expiration);

        userRepository.save(existUser);

        try {
            // Invio otp email
            emailService.sendOtpEmail(existUser.getEmail(), otp);
        } catch (Exception ex){
            // Usare un exception con MailmessagerEx
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Impossibile inviare l'email");
        }
    }

    @Override
    public void verifyOtp(OtpDto request, String email) {
        UserModel existUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));

        if (existUser.getVerifyOtp() == null || !existUser.getVerifyOtp().equals(request.getOtp())){
            throw new InvalidOtp("OTP non valido");
        }

        if (existUser.getVerifyOtpExpireAt() < System.currentTimeMillis()) {
            throw new OtpExpired("OTP scauduto");
        }

        existUser.setIsAccountVerify(true);
        existUser.setVerifyOtp(null);
        existUser.setVerifyOtpExpireAt(0L);

        userRepository.save(existUser);
    }
    
}
