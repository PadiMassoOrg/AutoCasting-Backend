package com.padimasso.autocasting.application.plan.repository;

import com.padimasso.autocasting.application.plan.model.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlanRepository extends JpaRepository<PlanEntity, UUID> {
    Optional<PlanEntity> findByCode(String code);
}
