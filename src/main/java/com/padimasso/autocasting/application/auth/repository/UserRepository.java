package com.padimasso.autocasting.application.auth.repository;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends SoftDeleteRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
