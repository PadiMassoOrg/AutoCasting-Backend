package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.response.AdminProposalRowResponse;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalLinkResponse;
import com.padimasso.autocasting.application.proposal.model.ProposalStatus;

import java.util.List;
import java.util.UUID;

public interface AdminProposalService {
    PageResponse<AdminProposalRowResponse> listProposals(
        int page,
        int size,
        String q,
        List<UUID> typeIds,
        List<ProposalStatus> statuses
    );

    ProposalLinkResponse regenerateLink(UUID proposalId);

    void revoke(UUID proposalId);
}
