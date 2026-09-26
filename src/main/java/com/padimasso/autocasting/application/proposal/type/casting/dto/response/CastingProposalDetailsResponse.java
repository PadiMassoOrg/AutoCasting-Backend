package com.padimasso.autocasting.application.proposal.type.casting.dto.response;

import com.padimasso.autocasting.application.admin.dto.response.AdminCastingDetailsResponse;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.proposal.model.ProposalProgress;
import com.padimasso.autocasting.application.proposal.model.ProposalStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CastingProposalDetailsResponse(
    UUID proposalId,
    String token,
    ProposalStatus status,
    ProposalProgress progress,
    String contactName,
    String contactEmail,
    String contactWhatsapp,
    String companyNameHint,
    String internalNotes,
    LocalDateTime firstOpenedAt,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    AdminCastingDetailsResponse casting,
    List<CastingRoleResponse> roles
) {
}
