package com.robertosodini.authify.repository;

import com.robertosodini.authify.model.UserModel;
import com.robertosodini.authify.model.VerifyOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface VerifyOtpRepository extends JpaRepository<VerifyOtp, Long> {

    @Modifying
    @Query("UPDATE VerifyOtp vo " +
            "SET vo.revoked = true " +
            "WHERE vo.user = :user")
    void revokeAllByUser(@Param("user") UserModel user);

    @Modifying
    @Query("UPDATE VerifyOtp vo " +
            "SET vo.revoked = true " +
            "WHERE vo.user = :user " +
            "AND vo.verifyAccountOtp = :otp " +
            "AND vo.revoked = false " +
            "AND vo.expiryOtp > :now")
    int verifyAndRevokeOtp(@Param("user") UserModel user, @Param("otp") String otp, @Param("now") LocalDateTime now);
}
