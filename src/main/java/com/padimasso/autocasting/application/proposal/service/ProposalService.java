package com.padimasso.autocasting.application.proposal.service;

import com.padimasso.autocasting.application.proposal.dto.response.ProposalClaimResult;
import com.padimasso.autocasting.application.proposal.dto.response.PublicProposalResponse;

public interface ProposalService {
    PublicProposalResponse getByToken(String token);

    void attach(String token);

    ProposalClaimResult claim(String token);

    ProposalClaimResult getClaimResult(String token);
}
