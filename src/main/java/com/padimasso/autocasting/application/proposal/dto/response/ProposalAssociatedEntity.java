package com.padimasso.autocasting.application.proposal.dto.response;

import com.padimasso.autocasting.application.common.model.EntityType;

import java.util.UUID;

public record ProposalAssociatedEntity(
    EntityType entityType,
    UUID id,
    String slug
) {
}
