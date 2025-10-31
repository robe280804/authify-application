package com.robertosodini.authify.service;

import com.robertosodini.authify.model.ResetOtp;
import com.robertosodini.authify.model.UserModel;
import com.robertosodini.authify.repository.ResetOtpRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ResetOtpService {

    private final ResetOtpRepository resetOtpRepository;

    public String create(UserModel user){
        // Genero otp a 6 cifre
        String otp =  String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        // Eseguo hashing
        String hashOtp = DigestUtils.sha3_256Hex(otp);
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(15);

        // Imposto come revoked tutti quelli precedenti, per evitare conflitti
        resetOtpRepository.revokeAllByUser(user);

        ResetOtp resetOtp = ResetOtp.builder()
                .user(user)
                .resetPasswordOtp(hashOtp)
                .expiryOtp(expiration)
                .revoked(false)
                .build();


    }
}
