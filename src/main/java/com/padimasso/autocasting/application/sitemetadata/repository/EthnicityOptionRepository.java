package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.EthnicityOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface EthnicityOptionRepository extends SoftDeleteRepository<EthnicityOptionEntity, UUID> {
}
