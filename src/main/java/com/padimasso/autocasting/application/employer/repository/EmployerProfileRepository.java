package com.padimasso.autocasting.application.employer.repository;

import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface EmployerProfileRepository extends SoftDeleteRepository<EmployerProfileEntity, UUID>, JpaSpecificationExecutor<EmployerProfileEntity> {

    Optional<EmployerProfileEntity> findByUserId(UUID id);

}
