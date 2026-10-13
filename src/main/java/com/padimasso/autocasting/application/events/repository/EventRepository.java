package com.padimasso.autocasting.application.events.repository;

import com.padimasso.autocasting.application.events.model.EventEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends SoftDeleteRepository<EventEntity, UUID> {

    @Override
    Page<EventEntity> findAll(@Nullable Specification<EventEntity> spec, Pageable pageable);

    // Images that must stay in storage: the ones of events that are neither deleted nor ended.
    @Query("select e.imageUrl from EventEntity e where e.deleted = false and e.endDate >= :today")
    List<String> findLiveImageUrls(@Param("today") LocalDate today);

    // Same visibility rule as the catalog: not deleted, not owned by a suspended user and not ended yet.
    @Query("select e from EventEntity e join fetch e.entryAccess join e.user u where e.slug = :slug and e.deleted = false and u.suspended = false and e.endDate >= :today")
    Optional<EventEntity> findPublicBySlug(@Param("slug") String slug, @Param("today") LocalDate today);

    // Admin lookup: no visibility rules besides deleted events, so ended events and events of suspended owners resolve.
    @Query("select e from EventEntity e join fetch e.entryAccess where e.slug = :slug and e.deleted = false")
    Optional<EventEntity> findAdminBySlug(@Param("slug") String slug);
}
