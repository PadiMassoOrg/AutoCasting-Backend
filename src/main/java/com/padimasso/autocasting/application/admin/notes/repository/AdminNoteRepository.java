package com.padimasso.autocasting.application.admin.notes.repository;

import com.padimasso.autocasting.application.admin.notes.model.AdminNoteEntity;
import com.padimasso.autocasting.application.admin.notes.model.AdminNoteEntityType;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminNoteRepository extends SoftDeleteRepository<AdminNoteEntity, UUID> {

    @EntityGraph(attributePaths = {"authorUser", "relatedUser"})
    Page<AdminNoteEntity> findAllByEntityTypeAndEntityId(
        AdminNoteEntityType entityType,
        UUID entityId,
        Pageable pageable
    );

    @EntityGraph(attributePaths = {"authorUser", "relatedUser"})
    Page<AdminNoteEntity> findAllByRelatedUser_Id(
        UUID relatedUserId,
        Pageable pageable
    );
}
