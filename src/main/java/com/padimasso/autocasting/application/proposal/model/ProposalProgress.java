package com.padimasso.autocasting.application.proposal.model;

import java.time.LocalDateTime;

// Derived (never persisted) progress of a PENDING proposal, shown as a badge in the Admin list.
// Each step supersedes the previous one: an attached account implies the link was opened.
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
