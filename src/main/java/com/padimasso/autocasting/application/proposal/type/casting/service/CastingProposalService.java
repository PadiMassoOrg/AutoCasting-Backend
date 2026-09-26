package com.padimasso.autocasting.application.proposal.type.casting.service;

import com.padimasso.autocasting.application.proposal.type.casting.dto.request.CastingProposalCreateRequest;
import com.padimasso.autocasting.application.proposal.type.casting.dto.response.CastingProposalCreatedResponse;
import com.padimasso.autocasting.application.proposal.type.casting.dto.response.CastingProposalDetailsResponse;

import java.util.UUID;

public interface CastingProposalService {
    CastingProposalCreatedResponse create(CastingProposalCreateRequest request);

    CastingProposalDetailsResponse getDetails(UUID proposalId);

    CastingProposalDetailsResponse update(UUID proposalId, CastingProposalCreateRequest request);
}
