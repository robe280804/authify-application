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
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final PasswordEncoder encoder;

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

    @Override
    public UserResponseDto getUserInfo(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::convertToUserResponse)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));
    }

    @Override
    public void sendResetOtp(String email) {
        UserModel existUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));

        // Genero otp a 6 cifre
       String otp =  String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
       
       // Expire time 15 minuti             
        long expiration = System.currentTimeMillis() + (15 * 60 * 1000);
        
        // TODO: Eseguire in un query sola dopo per evitare N+1
        existUser.setResetOtp(otp);
        existUser.setResetOtpExpireAt(expiration);
        userRepository.save(existUser);

        try {
             // Invio otp email
             emailService.sendResetOtpEmail(existUser.getEmail(), otp);
        } catch (Exception ex){
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Impossibile inviare l'email");
        }
    }

    @Override
    public void resetPassword(PasswordRequestDto request) {
        UserModel existUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + request.getEmail()));

        // TODO: Eseguire in un query sola dopo per evitare N+1
        if (existUser.getResetOtp() == null || !existUser.getResetOtp().equals(request.getOtp())){
            throw new InvalidOtp("OTP non valido");
        }

        if (existUser.getResetOtpExpireAt() < System.currentTimeMillis()){
            throw new OtpExpired("OTP scaduto");
        }

        // TODO: Eseguire in un query sola dopo per evitare N+1 e utilizzare un Model separato per il OTP
        existUser.setPassword(encoder.encode(request.getNewPassword()));
        existUser.setResetOtp(null);
        existUser.setResetOtpExpireAt(0L);
        userRepository.save(existUser);
    }

}
