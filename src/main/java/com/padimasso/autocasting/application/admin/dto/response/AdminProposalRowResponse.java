package com.padimasso.autocasting.application.admin.dto.response;

import com.padimasso.autocasting.application.proposal.dto.response.ProposalAssociatedEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalProgress;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AdminProposalRowResponse(
    UUID id,
    SiteMetadataObject type,
    String token,
    ProposalProgress progress,
    String contactName,
    String contactEmail,
    String contactWhatsapp,
    LocalDateTime modifiedAt,
    LocalDateTime claimedAt,
    String claimedByEmail,
    List<ProposalAssociatedEntity> associated
) {
}
