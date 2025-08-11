package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.EducationEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface EducationRepository extends SoftDeleteRepository<EducationEntity, UUID> {
}
