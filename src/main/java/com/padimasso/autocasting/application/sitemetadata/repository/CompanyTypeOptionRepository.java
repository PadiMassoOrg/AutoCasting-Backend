package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.CompanyTypeOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface CompanyTypeOptionRepository extends SoftDeleteRepository<CompanyTypeOptionEntity, UUID> {
}
