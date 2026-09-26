package com.padimasso.autocasting.application.proposal.type;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalAssociatedEntity;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalClaimResult;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalRequirement;

import java.util.List;

public interface ProposalTypeHandler {

    String typeCode();

    ProposalRequirement requirement();

    Object buildPreview(ProposalEntity proposal);

    void associate(ProposalEntity proposal, UserEntity user);

    ProposalClaimResult describeClaim(ProposalEntity proposal);

    List<ProposalAssociatedEntity> associatedEntities(ProposalEntity proposal);

    void discard(ProposalEntity proposal);
}
