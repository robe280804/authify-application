package com.robertosodini.authify.repository;

import com.robertosodini.authify.model.ResetOtp;
import com.robertosodini.authify.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ResetOtpRepository extends JpaRepository<ResetOtp, Long> {

    @Modifying
    @Query("UPDATE ResetOtp ro " +
            "SET ro.revoked = true " +
            "WHERE ro.user = :user")
    void revokeAllByUser(@Param("user") UserModel user);

    @Modifying
    @Query("UPDATE ResetOtp ro " +
            "SET ro.revoked = true " +
            "WHERE ro.user = :user " +
            "AND ro.resetPasswordOtp = :otp " +
            "AND ro.revoked = false " +
            "AND ro.expiryOtp > :now")
    int verifyAndRevokeOtp(@Param("user") UserModel user, @Param("otp") String otp, @Param("now") LocalDateTime now);
}
