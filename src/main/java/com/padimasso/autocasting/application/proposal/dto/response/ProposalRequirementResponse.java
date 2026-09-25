package com.padimasso.autocasting.application.proposal.dto.response;

import com.padimasso.autocasting.application.auth.model.UserMode;
import com.padimasso.autocasting.application.proposal.model.ProposalRequirement;

public record ProposalRequirementResponse(
    ProposalRequirement code,
    UserMode requiredMode
) {
    public static ProposalRequirementResponse of(ProposalRequirement requirement) {
        return new ProposalRequirementResponse(requirement, requirement.requiredMode());
    }
}
