package com.padimasso.autocasting.application.proposal.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProposalProgressTest {

    @Test
    void of_neverOpenedAndNoAttachments_returnsLinkGenerated() {
        assertEquals(ProposalProgress.LINK_GENERATED, ProposalProgress.of(ProposalStatus.PENDING, null, false));
    }

    @Test
    void of_openedAndNoAttachments_returnsLinkOpened() {
        assertEquals(ProposalProgress.LINK_OPENED, ProposalProgress.of(ProposalStatus.PENDING, LocalDateTime.now(), false));
    }

    @Test
    void of_openedWithAttachments_returnsAccountAttached() {
        assertEquals(ProposalProgress.ACCOUNT_ATTACHED, ProposalProgress.of(ProposalStatus.PENDING, LocalDateTime.now(), true));
    }

    @Test
    void of_attachmentsWinEvenWithoutFirstOpenedAt() {
        assertEquals(ProposalProgress.ACCOUNT_ATTACHED, ProposalProgress.of(ProposalStatus.PENDING, null, true));
    }

    @Test
    void of_claimed_winsOverEverythingElse() {
        assertEquals(ProposalProgress.CLAIMED, ProposalProgress.of(ProposalStatus.CLAIMED, LocalDateTime.now(), true));
    }
}
