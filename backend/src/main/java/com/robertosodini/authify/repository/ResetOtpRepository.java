package com.robertosodini.authify.repository;

import com.robertosodini.authify.model.ResetOtp;
import com.robertosodini.authify.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetOtpRepository extends JpaRepository<ResetOtp, Long> {

    @Modifying
    @Query("UPDATE ResetOtp ro " +
            "SET ro.revoked = true " +
            "WHERE ro.user = :user")
    void revokeAllByUser(@Param("user") UserModel user);
}
