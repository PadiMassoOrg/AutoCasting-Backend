package com.padimasso.autocasting.application.proposal.dto.response;

public record PublicProposalResponse(
    String typeCode,
    ProposalRequirementResponse requirement,
    Object preview
) {
}
