package com.padimasso.autocasting.application.proposal.model;

import java.time.LocalDateTime;

public enum ProposalProgress {
    LINK_GENERATED,
    LINK_OPENED,
    ACCOUNT_ATTACHED;

    public static ProposalProgress of(LocalDateTime firstOpenedAt, boolean hasAttachments) {
        if (hasAttachments) return ACCOUNT_ATTACHED;
        if (firstOpenedAt != null) return LINK_OPENED;
        return LINK_GENERATED;
    }
}
