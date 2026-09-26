package com.padimasso.autocasting.application.admin.repository.specification;

import com.padimasso.autocasting.application.proposal.model.ProposalStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminProposalSpecsTest {

    @Test
    void listableStatuses_noneRequested_returnsPendingAndClaimed() {
        assertEquals(Set.of(ProposalStatus.PENDING, ProposalStatus.CLAIMED), AdminProposalSpecs.listableStatuses(null));
        assertEquals(Set.of(ProposalStatus.PENDING, ProposalStatus.CLAIMED), AdminProposalSpecs.listableStatuses(List.of()));
    }

    @Test
    void listableStatuses_requested_keepsOnlyThose() {
        assertEquals(Set.of(ProposalStatus.CLAIMED), AdminProposalSpecs.listableStatuses(List.of(ProposalStatus.CLAIMED)));
    }

    @Test
    void listableStatuses_revokedRequested_isNeverListed() {
        assertTrue(AdminProposalSpecs.listableStatuses(List.of(ProposalStatus.REVOKED)).isEmpty());
        assertEquals(
            Set.of(ProposalStatus.PENDING),
            AdminProposalSpecs.listableStatuses(List.of(ProposalStatus.PENDING, ProposalStatus.REVOKED))
        );
    }
}
