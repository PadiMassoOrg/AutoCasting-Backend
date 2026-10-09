package com.padimasso.autocasting.application.history.repository;

import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.model.HistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.UUID;

public interface HistoryRepository extends JpaRepository<HistoryEntity, UUID> {

    Page<HistoryEntity> findAllByEntityTypeAndEntityIdAndDeletedFalse(EntityType entityType, UUID entityId, Pageable pageable);

    @Query("""
        select h
        from HistoryEntity h
        where h.deleted = false
          and ((h.entityType = :entityType and h.entityId = :entityId)
            or (h.entityType = :relatedType and h.entityId in :relatedIds))
        """)
    Page<HistoryEntity> findAllByEntityOrRelatedEntities(
        @Param("entityType") EntityType entityType,
        @Param("entityId") UUID entityId,
        @Param("relatedType") EntityType relatedType,
        @Param("relatedIds") Collection<UUID> relatedIds,
        Pageable pageable
    );
}
