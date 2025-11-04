package com.robertosodini.authify.service;

import com.robertosodini.authify.exceptions.InvalidOtp;
import com.robertosodini.authify.model.ResetOtp;
import com.robertosodini.authify.model.UserModel;
import com.robertosodini.authify.model.VerifyOtp;
import com.robertosodini.authify.repository.VerifyOtpRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class VerifyOtpService {

    private final VerifyOtpRepository verifyOtpRepository;

    /// Genero OTP a 6 cifre, eseguendo l'hashing
    /// Imposto gli OTP precedenti come revoked per evitare conflitti
    /// Salvo il nuovo OTP
    @Transactional
    public String create(UserModel user){

        String otp =  String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        String hashOtp = DigestUtils.sha3_256Hex(otp);
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(15);

        verifyOtpRepository.revokeAllByUser(user);

        VerifyOtp verifyOtp = VerifyOtp.builder()
                .user(user)
                .verifyAccountOtp(hashOtp)
                .expiryOtp(expiration)
                .revoked(false)
                .build();

        verifyOtpRepository.save(verifyOtp);
        return otp;
    }

    @Transactional
    public void verifyOtp(UserModel user, String otp){
        String hashOtp = DigestUtils.sha3_256Hex(otp);
        // Eseguo tutto in un unica query (controllo se esiste, se valido, e lo imposto come revoked)
        int updated = verifyOtpRepository.verifyAndRevokeOtp(user, hashOtp, LocalDateTime.now());
        if (updated <= 0) {
            throw new InvalidOtp("Il tuo OTP non è più valido o è già stato usato.");
        }
    }
}
