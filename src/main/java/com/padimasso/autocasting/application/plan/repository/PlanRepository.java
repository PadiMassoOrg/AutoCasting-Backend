package com.padimasso.autocasting.application.plan.repository;

import com.padimasso.autocasting.application.plan.model.PlanEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlanRepository extends SoftDeleteRepository<PlanEntity, UUID> {
    Optional<PlanEntity> findByCode(String code);
}
