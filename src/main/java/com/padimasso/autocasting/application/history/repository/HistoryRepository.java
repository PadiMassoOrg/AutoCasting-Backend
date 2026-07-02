package com.padimasso.autocasting.application.history.repository;

import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.model.HistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HistoryRepository extends JpaRepository<HistoryEntity, UUID> {

    Page<HistoryEntity> findAllByEntityTypeAndEntityIdAndDeletedFalse(EntityType entityType, UUID entityId, Pageable pageable);
}
