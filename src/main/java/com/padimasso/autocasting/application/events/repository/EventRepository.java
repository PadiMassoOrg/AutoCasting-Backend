package com.padimasso.autocasting.application.events.repository;

import com.padimasso.autocasting.application.events.model.EventEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends SoftDeleteRepository<EventEntity, UUID> {

    @Override
    Page<EventEntity> findAll(@Nullable Specification<EventEntity> spec, Pageable pageable);

    // Same visibility rule as the catalog: not deleted and not owned by a suspended user.
    @Query("select e from EventEntity e join fetch e.entryAccess join e.user u where e.slug = :slug and e.deleted = false and u.suspended = false")
    Optional<EventEntity> findPublicBySlug(@Param("slug") String slug);
}
