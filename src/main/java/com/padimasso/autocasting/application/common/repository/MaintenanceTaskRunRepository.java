package com.padimasso.autocasting.application.common.repository;

import com.padimasso.autocasting.application.common.model.MaintenanceTaskRunEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface MaintenanceTaskRunRepository extends SoftDeleteRepository<MaintenanceTaskRunEntity, UUID> {
    Optional<MaintenanceTaskRunEntity> findByTaskCode(String taskCode);
}
