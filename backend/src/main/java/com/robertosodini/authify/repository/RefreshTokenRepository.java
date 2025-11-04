package com.robertosodini.authify.repository;

import com.robertosodini.authify.model.RefreshToken;
import com.robertosodini.authify.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.userEmail = :userEmail")
    void revokedAllForUser(@Param("userEmail") String email);

    // True se esiste un refresh token valido, altrimenti false
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM RefreshToken r " +
            "WHERE r.userEmail = :userEmail AND r.revoked = false AND r.expiryDate > :now")
    Boolean isRefreshValid(@Param("userEmail") String email, @Param("now")LocalDateTime date);
}
