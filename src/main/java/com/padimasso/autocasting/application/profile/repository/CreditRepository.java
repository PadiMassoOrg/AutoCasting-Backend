package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.CreditEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface CreditRepository extends SoftDeleteRepository<CreditEntity, UUID> {
}
