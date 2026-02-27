package com.padimasso.autocasting.application.applications.repository;

import com.padimasso.autocasting.application.applications.model.CastingApplicationRequirementSubmissionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface CastingApplicationRequirementSubmissionRepository extends SoftDeleteRepository<CastingApplicationRequirementSubmissionEntity, UUID> {
}
