package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.CurrencyOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface CurrencyOptionRepository extends SoftDeleteRepository<CurrencyOptionEntity, UUID> {
}
