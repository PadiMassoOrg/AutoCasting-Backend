package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.ProductionTypeEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface ProductionTypeRepository extends SoftDeleteRepository<ProductionTypeEntity, UUID> {
}
