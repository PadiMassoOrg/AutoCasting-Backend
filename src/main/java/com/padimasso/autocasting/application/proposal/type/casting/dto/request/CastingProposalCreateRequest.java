package com.padimasso.autocasting.application.proposal.type.casting.dto.request;

import com.padimasso.autocasting.application.castings.dto.request.CastingUpsertRequest;
import com.padimasso.autocasting.application.proposal.dto.request.ProposalInternalReferenceRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CastingProposalCreateRequest(
    @NotNull(message = "proposals.casting.incomplete")
    @Valid
    CastingUpsertRequest casting,
    @NotEmpty(message = "proposals.casting.incomplete")
    List<@Valid @NotNull CastingProposalRoleRequest> roles,
    @Valid
    ProposalInternalReferenceRequest internalReference
) {
}
