package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.ColorOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface ColorOptionRepository extends SoftDeleteRepository<ColorOptionEntity, UUID> {
}
