package com.robertosodini.authify.service;

import com.robertosodini.authify.exceptions.InvalidOtp;
import com.robertosodini.authify.model.ResetOtp;
import com.robertosodini.authify.model.UserModel;
import com.robertosodini.authify.repository.ResetOtpRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ResetOtpService {

    private final ResetOtpRepository resetOtpRepository;

    @Transactional
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

        return resetOtp.getResetPasswordOtp();
    }

    @Transactional
    public void verifyOtp(UserModel user, String otp){
        // Eseguo tutto in un unica query (controllo se esiste, se valido, e lo imposto come revoked)
        int updated = resetOtpRepository.verifyAndRevokeOtp(user, otp, LocalDateTime.now());
        if (updated <= 0) {
            throw new InvalidOtp("Il tuo OTP non è più valido o è già stato usato.");
        }
    }
}
