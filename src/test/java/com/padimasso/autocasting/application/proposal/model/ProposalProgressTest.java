package com.padimasso.autocasting.application.proposal.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProposalProgressTest {

    @Test
    void of_neverOpenedAndNoAttachments_returnsLinkGenerated() {
        assertEquals(ProposalProgress.LINK_GENERATED, ProposalProgress.of(null, false));
    }

    @Test
    void of_openedAndNoAttachments_returnsLinkOpened() {
        assertEquals(ProposalProgress.LINK_OPENED, ProposalProgress.of(LocalDateTime.now(), false));
    }

    @Test
    void of_openedWithAttachments_returnsAccountAttached() {
        assertEquals(ProposalProgress.ACCOUNT_ATTACHED, ProposalProgress.of(LocalDateTime.now(), true));
    }

    @Test
    void of_attachmentsWinEvenWithoutFirstOpenedAt() {
        assertEquals(ProposalProgress.ACCOUNT_ATTACHED, ProposalProgress.of(null, true));
    }
}
