package com.padimasso.autocasting.application.employer.repository;

import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface EmployerBasicInfoRepository extends SoftDeleteRepository<EmployerBasicInfoEntity, UUID> {
}
