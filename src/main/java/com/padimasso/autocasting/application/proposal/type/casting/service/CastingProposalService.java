package com.padimasso.autocasting.application.proposal.type.casting.service;

import com.padimasso.autocasting.application.proposal.type.casting.dto.request.CastingProposalCreateRequest;
import com.padimasso.autocasting.application.proposal.type.casting.dto.response.CastingProposalCreatedResponse;

public interface CastingProposalService {
    CastingProposalCreatedResponse create(CastingProposalCreateRequest request);
}
