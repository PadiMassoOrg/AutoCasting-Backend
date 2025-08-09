package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.ProfessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProfessionRepository extends JpaRepository<ProfessionEntity, UUID> {
    List<ProfessionEntity> findAllByDeletedFalse();
}
