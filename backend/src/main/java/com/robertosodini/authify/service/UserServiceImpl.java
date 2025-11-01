package com.robertosodini.authify.service;

import com.robertosodini.authify.dto.PasswordRequestDto;
import com.robertosodini.authify.dto.UserRequestDto;
import com.robertosodini.authify.dto.UserResponseDto;
import com.robertosodini.authify.exceptions.EmailAlredyRegister;
import com.robertosodini.authify.exceptions.InvalidOtp;
import com.robertosodini.authify.exceptions.OtpExpired;
import com.robertosodini.authify.exceptions.PasswordUpdate;
import com.robertosodini.authify.mapper.UserMapper;
import com.robertosodini.authify.model.UserModel;
import com.robertosodini.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final PasswordEncoder encoder;
    private final ResetOtpService resetOtpService;

    /// Registrazione
    @Override
    public UserResponseDto createUser(UserRequestDto request) {
        log.info("[REGISTER_USER] Registrazione utente con email: {} in esecuizione", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())){
            log.warn("[REGISTER_USER] Registrazione fallita, email {} già registrata", request.getEmail());
            throw new EmailAlredyRegister("Email già registrata");
        }
        UserModel userModel = userMapper.convertToUserEntity(request);
        UserModel newUserModel = userRepository.save(userModel);
        // Invio email
        emailService.sendWelcomeEmail(newUserModel.getEmail(), newUserModel.getName());

        log.info("[REGISTER_USER] Registrazione avvenuta con successo per {}", request.getEmail());
        return userMapper.convertToUserResponse(newUserModel);
    }

    ///  Info user
    @Override
    public UserResponseDto getUserInfo(String email) {
        log.info("[USER_PROFILE_INFO] In esecuzione per {}", email);
        return userRepository.findByEmail(email)
                .map(userMapper::convertToUserResponse)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));
    }

    /// Reset password OTP
    @Override
    public String sendResetOtp(String email) {
        log.info("[RESET_PASSWORD_OTP] Richiesta reset password per {}", email);
        UserModel existUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));

       String otp = resetOtpService.create(existUser);
       emailService.sendResetOtpEmail(existUser.getEmail(), otp);

       log.info("[RESET_PASSWORD_OTP] Email con OTP inviata con successo a {}", email);
       return "Ti è stata inviata un email per resettare la password";
    }

    /// Conferma reset password
    @Override
    @Transactional
    public String resetPassword(PasswordRequestDto request) {
        log.info("[RESET_PASSWORD_OTP] Salvataggio della nuova password in esecuzione");
        UserModel existUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + request.getEmail()));

        resetOtpService.verifyOtp(existUser, request.getOtp());

        int updated = userRepository.setNewPassword(existUser.getId(), encoder.encode(request.getNewPassword()));
        if (updated <= 0) {
            throw new PasswordUpdate("Non è stato possibile aggiornare la password");
        }

        log.info("[RESET_PASSWORD_OTP] Password aggiornata con successo per {}", existUser.getEmail());
        return "Password aggiornata con successo";
    }

}
