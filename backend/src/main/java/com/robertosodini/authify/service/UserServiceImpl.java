package com.robertosodini.authify.service;

import com.robertosodini.authify.dto.PasswordRequestDto;
import com.robertosodini.authify.dto.UserRequestDto;
import com.robertosodini.authify.dto.UserResponseDto;
import com.robertosodini.authify.exceptions.EmailAlredyRegister;
import com.robertosodini.authify.exceptions.InvalidOtp;
import com.robertosodini.authify.exceptions.OtpExpired;
import com.robertosodini.authify.mapper.UserMapper;
import com.robertosodini.authify.model.UserModel;
import com.robertosodini.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final PasswordEncoder encoder;
    private final ResetOtpService resetOtpService;

    /// Registrazione
    @Override
    public UserResponseDto createUser(UserRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlredyRegister("Email già registrata");
        }
        UserModel userModel = userMapper.convertToUserEntity(request);
        UserModel newUserModel = userRepository.save(userModel);
        // Invio email
        emailService.sendWelcomeEmail(newUserModel.getEmail(), newUserModel.getName());
        return userMapper.convertToUserResponse(newUserModel);
    }

    ///  Info user
    @Override
    public UserResponseDto getUserInfo(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::convertToUserResponse)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));
    }

    /// Reset password OTP
    @Override
    public void sendResetOtp(String email) {
        UserModel existUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));

       String otp = resetOtpService.create(existUser);
       emailService.sendResetOtpEmail(existUser.getEmail(), otp);
    }

    // Conferma reset passsword
    @Override
    public String resetPassword(PasswordRequestDto request) {
        UserModel existUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + request.getEmail()));

        resetOtpService.verifyOtp(existUser, request.getOtp());
        return "Password aggiornata con successo";
    }

}
