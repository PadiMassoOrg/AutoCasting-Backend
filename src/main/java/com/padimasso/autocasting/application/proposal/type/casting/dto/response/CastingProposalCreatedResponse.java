package com.padimasso.autocasting.application.proposal.type.casting.dto.response;

import java.util.UUID;

public record CastingProposalCreatedResponse(
    UUID proposalId,
    String token
) {
}
