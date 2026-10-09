package com.padimasso.autocasting.application.history.service;

import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.model.HistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.UUID;

public interface HistoryService {

    void createHistoryEntry(EntityType entityType, UUID entityId, String note, Object changes);

    Page<HistoryEntity> listHistoryByEntity(EntityType entityType, UUID entityId, Pageable pageable);

    /** The entity's own history together with the history of the related entities (e.g. a casting and its roles). */
    Page<HistoryEntity> listHistoryByEntityAndRelated(
        EntityType entityType,
        UUID entityId,
        EntityType relatedType,
        Collection<UUID> relatedIds,
        Pageable pageable
    );
}
