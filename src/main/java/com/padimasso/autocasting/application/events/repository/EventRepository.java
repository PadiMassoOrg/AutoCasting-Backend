package com.padimasso.autocasting.application.events.repository;

import com.padimasso.autocasting.application.events.model.EventEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface EventRepository extends SoftDeleteRepository<EventEntity, UUID> {

    @Override
    Page<EventEntity> findAll(@Nullable Specification<EventEntity> spec, Pageable pageable);
}
