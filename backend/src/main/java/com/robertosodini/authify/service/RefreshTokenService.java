package com.robertosodini.authify.service;


import com.robertosodini.authify.model.RefreshToken;
import com.robertosodini.authify.repository.RefreshTokenRepository;
import com.robertosodini.authify.repository.UserRepository;
import com.robertosodini.authify.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    /// Revoco tutti i refresh token precedenti per evitare conflitti
    /// Creo un refresh token, eseguo l'hashing e lo salvo nel db
    @Transactional
    public void generateToken(UserDetails userDetails){

        refreshTokenRepository.revokedAllForUser(userDetails.getUsername());  // Imposto i refresh-token precedenti come revoked

        String refreshToken = jwtUtil.generateToken(false, userDetails);
        String hashRefreshToken = DigestUtils.sha3_256Hex(refreshToken);

        RefreshToken refreshTokenModel = RefreshToken.builder()
                .userEmail(userDetails.getUsername())
                .refreshToken(hashRefreshToken)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenModel);
    }
}
