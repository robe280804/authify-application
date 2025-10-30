package com.robertosodini.authify.repository;

import com.robertosodini.authify.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {

    Optional<UserModel> findByEmail(String email);

    boolean existsByEmail(String email);
}
