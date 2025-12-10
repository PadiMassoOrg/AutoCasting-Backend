package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.RoleTypeOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface RoleTypeOptionRepository extends SoftDeleteRepository<RoleTypeOptionEntity, UUID> {
}
