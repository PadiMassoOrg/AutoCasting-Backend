package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.CharacteristicsEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface CharacteristicsRepository extends SoftDeleteRepository<CharacteristicsEntity, UUID> {
}
