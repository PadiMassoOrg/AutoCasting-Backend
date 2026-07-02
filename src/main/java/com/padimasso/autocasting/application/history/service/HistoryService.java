package com.padimasso.autocasting.application.history.service;

import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.model.HistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HistoryService {

    void createHistoryEntry(EntityType entityType, UUID entityId, String note);

    Page<HistoryEntity> listHistoryByEntity(EntityType entityType, UUID entityId, Pageable pageable);
}
