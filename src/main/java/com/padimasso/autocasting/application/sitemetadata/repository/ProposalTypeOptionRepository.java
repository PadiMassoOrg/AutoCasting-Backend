package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.ProposalTypeOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProposalTypeOptionRepository extends SoftDeleteRepository<ProposalTypeOptionEntity, UUID> {
    Optional<ProposalTypeOptionEntity> findByStringCode(String stringCode);
}
