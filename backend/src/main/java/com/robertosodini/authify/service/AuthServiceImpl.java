package com.robertosodini.authify.service;

import com.robertosodini.authify.dto.AuthRequestDto;
import com.robertosodini.authify.dto.AuthResponseDto;
import com.robertosodini.authify.dto.LoginHistoryDto;
import com.robertosodini.authify.dto.OtpDto;
import com.robertosodini.authify.exceptions.InvalidOtp;
import com.robertosodini.authify.exceptions.OtpExpired;
import com.robertosodini.authify.exceptions.VerificationUpdate;
import com.robertosodini.authify.kafka.LoginHistoryProducer;
import com.robertosodini.authify.model.LoginHistory;
import com.robertosodini.authify.model.UserModel;
import com.robertosodini.authify.repository.UserRepository;
import com.robertosodini.authify.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final VerifyOtpService verifyOtpService;
    private final LoginHistoryService loginHistoryService;
    private final LoginHistoryProducer loginHistoryProducer;


    @Override
    public AuthResponseDto login(@Valid AuthRequestDto request, HttpServletRequest httpRequest) {
        log.info("[LOGIN_USER] Login in esecuzione per {}", request.getEmail());

        LoginHistoryDto loginHistory = loginHistoryService.create(httpRequest, request.getEmail());

        Authentication auth = authenticate(request.getEmail(), request.getPassword(), loginHistory);
        final UserDetails userDetails = (UserDetails) auth.getPrincipal();
        final String token = jwtUtil.generateToken(userDetails);

        loginHistory.setSuccess(true);
        sendLoginHistory(loginHistory);  // Invio a kafka per salvataggio async

        log.info("[LOGIN_USER] Login andato a buon fine per {}", request.getEmail());
        return new AuthResponseDto(userDetails.getUsername(), token);
    }


    private Authentication authenticate(String email, String password, LoginHistoryDto loginHistory) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            return auth;

        } catch(BadCredentialsException ex) {
            log.warn("[LOGIN_USER] Login fallito per {}, credenziali errate", email);
            sendFailureLoginHistory(loginHistory, "Credenziali errate");
            throw new BadCredentialsException("Email o password errati");
        } catch(DisabledException ex) {
            log.warn("[LOGIN_USER] Login fallito per {}, account disabilitato", email);
            sendFailureLoginHistory(loginHistory, "Account disabilitato");
            throw new DisabledException("Account disabilitato");
        } catch(Exception ex) {
            log.error("[LOGIN_USER] Login fallito per {}, messaggio: [{}]", email, ex.getMessage());
            sendFailureLoginHistory(loginHistory, "Errore lato server");
            throw new RuntimeException("Autenticazione fallita");
        }
    }

    private void sendFailureLoginHistory(LoginHistoryDto loginHistoryDto, String failureMsg){
        loginHistoryDto.setSuccess(false);
        loginHistoryDto.setFailureReason(failureMsg);
        sendLoginHistory(loginHistoryDto);
    }

    // Non chiamo subito il producer kafka nel metodo di login, altrimenti sarebbe dipendente dall'invio.
    // In questo la chiamata a producer non influenza il metodo di login con @Async
    @Async
    private void sendLoginHistory(LoginHistoryDto loginHistoryDto){
        loginHistoryProducer.sendLoginHistory(loginHistoryDto);
    }

    @Override
    public String sendOtp(String email) {
        log.info("[VERIFY_ACCOUNT_OTP] Verificazione dell'account per {}", email);
        UserModel existUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));

        // Controllo se è già autenticato
        if (existUser.getIsAccountVerify() != null && existUser.getIsAccountVerify()){
            log.info("[VERIFY_ACCOUNT_OTP] Account di {} già verificato", email);
            return "Il tuo account è già verificato";
        }
        String otp = verifyOtpService.create(existUser);
        emailService.sendOtpEmail(existUser.getEmail(), otp);

        log.info("[VERIFY_ACCOUNT_OTP] Invio email a {} con codice OTP", email);
        return "Ti è stata inviata un email per verificare l'account";
    }

    @Override
    @Transactional
    public void verifyOtp(OtpDto request, String email) {
        log.info("[VERIFY_ACCOUNT_OTP] Verifica dell' OTP in corso per {}", email);
        UserModel existUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));

        verifyOtpService.verifyOtp(existUser, request.getOtp());
        int updated = userRepository.setVerifyAccount(existUser.getId());

        if (updated <= 0){
            log.error("[VERIFY_ACCOUNT_OTP] Impossibile verificare l'account di {}", email);
            throw new VerificationUpdate("Non è stato possibile veriificare il tuo account");
        }
        log.info("[VERIFY_ACCOUNT_OTP] Account di {} verificato con successo", email);
    }

    @Override
    public String logout(String email, HttpServletResponse response) {
        log.info("[LOGOUT] Logout in esecuzione per {}", email);
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false) //true in prod
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        SecurityContextHolder.clearContext();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        log.info("[LOGOUT[ Logout avvenuto con successo per {}", email);
        return "Logout avvenuto con successo!";
    }

}
