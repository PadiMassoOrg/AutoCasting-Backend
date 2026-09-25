package com.padimasso.autocasting.application.proposal.service.internal;

import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalClaimResult;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalRequirement;
import com.padimasso.autocasting.application.proposal.model.ProposalStatus;
import com.padimasso.autocasting.application.proposal.repository.ProposalRepository;
import com.padimasso.autocasting.application.proposal.type.ProposalTypeHandler;
import com.padimasso.autocasting.application.proposal.type.ProposalTypeHandlerRegistry;
import com.padimasso.autocasting.application.sitemetadata.model.ProposalTypeOptionEntity;
import com.padimasso.autocasting.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.PROPOSAL_TYPE_CASTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProposalClaimServiceTest {

    @Mock
    private ProposalRepository proposalRepository;
    @Mock
    private ProposalTypeHandler castingHandler;

    private ProposalClaimService service;

    @BeforeEach
    void setUp() {
        lenient().when(castingHandler.typeCode()).thenReturn(PROPOSAL_TYPE_CASTING);
        lenient().when(castingHandler.requirement()).thenReturn(ProposalRequirement.EMPLOYER_ONBOARDING_COMPLETED);
        lenient().when(castingHandler.describeClaim(any()))
            .thenReturn(new ProposalClaimResult(PROPOSAL_TYPE_CASTING, List.of(), Map.of("published", true)));
        service = new ProposalClaimService(proposalRepository, new ProposalTypeHandlerRegistry(List.of(castingHandler)));
    }

    private ProposalEntity proposal(ProposalStatus status) {
        ProposalTypeOptionEntity type = new ProposalTypeOptionEntity();
        type.setStringCode(PROPOSAL_TYPE_CASTING);
        ProposalEntity proposal = ProposalEntity.builder().id(UUID.randomUUID()).type(type).status(status).build();
        lenient().when(proposalRepository.findByIdForUpdate(proposal.getId())).thenReturn(Optional.of(proposal));
        return proposal;
    }

    private UserEntity user(OnboardingStatus employerOnboardingStatus) {
        return UserEntity.builder().id(UUID.randomUUID()).employerOnboardingStatus(employerOnboardingStatus).build();
    }

    @Test
    void claimIfPending_pendingAndRequirementMet_associatesAndMarksClaimed() {
        ProposalEntity proposal = proposal(ProposalStatus.PENDING);
        UserEntity user = user(OnboardingStatus.COMPLETED);

        Optional<ProposalClaimResult> result = service.claimIfPending(proposal.getId(), user);

        assertTrue(result.isPresent());
        verify(castingHandler).associate(proposal, user);
        assertEquals(ProposalStatus.CLAIMED, proposal.getStatus());
        assertSame(user, proposal.getClaimedByUser());
        assertNotNull(proposal.getClaimedAt());
    }

    @Test
    void claimIfPending_alreadyClaimed_returnsEmptyAndAssociatesNothing() {
        ProposalEntity proposal = proposal(ProposalStatus.CLAIMED);

        assertTrue(service.claimIfPending(proposal.getId(), user(OnboardingStatus.COMPLETED)).isEmpty());
        verify(castingHandler, never()).associate(any(), any());
    }

    @Test
    void claimIfPending_revoked_returnsEmpty() {
        ProposalEntity proposal = proposal(ProposalStatus.REVOKED);

        assertTrue(service.claimIfPending(proposal.getId(), user(OnboardingStatus.COMPLETED)).isEmpty());
    }

    @Test
    void claimIfPending_secondClaimAfterFirst_returnsEmpty() {
        ProposalEntity proposal = proposal(ProposalStatus.PENDING);
        service.claimIfPending(proposal.getId(), user(OnboardingStatus.COMPLETED));

        assertTrue(service.claimIfPending(proposal.getId(), user(OnboardingStatus.COMPLETED)).isEmpty());
        verify(castingHandler).associate(any(), any());
    }

    @Test
    void claimIfPending_requirementNotMet_throwsConflictAndStaysPending() {
        ProposalEntity proposal = proposal(ProposalStatus.PENDING);

        ApiException ex = assertThrows(
            ApiException.class,
            () -> service.claimIfPending(proposal.getId(), user(OnboardingStatus.IN_PROGRESS))
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertEquals(ProposalStatus.PENDING, proposal.getStatus());
        verify(castingHandler, never()).associate(any(), any());
    }

    @Test
    void claimAttachedProposalsSatisfiedBy_claimsPendingOnesAndSkipsOthers() {
        ProposalEntity pending = proposal(ProposalStatus.PENDING);
        ProposalEntity claimedMeanwhile = proposal(ProposalStatus.CLAIMED);
        UserEntity user = user(OnboardingStatus.COMPLETED);
        when(proposalRepository.findPendingProposalIdsAttachedToUser(user.getId()))
            .thenReturn(List.of(pending.getId(), claimedMeanwhile.getId()));

        service.claimAttachedProposalsSatisfiedBy(user);

        verify(castingHandler).associate(pending, user);
        verify(castingHandler, never()).associate(claimedMeanwhile, user);
        assertEquals(ProposalStatus.CLAIMED, pending.getStatus());
    }

    @Test
    void claimAttachedProposalsSatisfiedBy_requirementNotMet_claimsNothing() {
        ProposalEntity pending = proposal(ProposalStatus.PENDING);
        UserEntity user = user(OnboardingStatus.NOT_STARTED);
        when(proposalRepository.findPendingProposalIdsAttachedToUser(user.getId())).thenReturn(List.of(pending.getId()));

        service.claimAttachedProposalsSatisfiedBy(user);

        verify(castingHandler, never()).associate(any(), any());
        assertEquals(ProposalStatus.PENDING, pending.getStatus());
    }
}
