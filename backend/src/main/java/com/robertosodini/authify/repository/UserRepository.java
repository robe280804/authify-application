package com.robertosodini.authify.repository;

import com.robertosodini.authify.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {

    Optional<UserModel> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE UserModel um SET um.password = :newPassword WHERE um.id = :id")
    int setNewPassword(@Param("id") Long id, @Param("newPassword") String newPassword);

    @Modifying
    @Query("UPDATE UserModel um SET um.isAccountVerify = true WHERE  um.id = :id")
    int setVerifyAccount(@Param("id") Long id);
}
