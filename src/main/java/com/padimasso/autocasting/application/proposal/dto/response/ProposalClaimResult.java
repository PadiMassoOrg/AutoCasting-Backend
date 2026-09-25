package com.padimasso.autocasting.application.proposal.dto.response;

import java.util.List;
import java.util.Map;

public record ProposalClaimResult(
    String typeCode,
    List<ProposalAssociatedEntity> associated,
    Map<String, Object> outcome
) {
}
