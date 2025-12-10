package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.ProjectTypeOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface ProjectTypeOptionRepository extends SoftDeleteRepository<ProjectTypeOptionEntity, UUID> {
}
