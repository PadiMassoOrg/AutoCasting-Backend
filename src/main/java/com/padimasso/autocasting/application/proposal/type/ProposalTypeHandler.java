package com.padimasso.autocasting.application.proposal.type;

import com.padimasso.autocasting.application.proposal.model.ProposalEntity;

public interface ProposalTypeHandler {

    String typeCode();

    void discard(ProposalEntity proposal);
}
