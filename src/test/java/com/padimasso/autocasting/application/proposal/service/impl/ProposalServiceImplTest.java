package com.padimasso.autocasting.application.proposal.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.model.UserMode;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalClaimResult;
import com.padimasso.autocasting.application.proposal.dto.response.PublicProposalResponse;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalRequirement;
import com.padimasso.autocasting.application.proposal.model.ProposalStatus;
import com.padimasso.autocasting.application.proposal.repository.ProposalRepository;
import com.padimasso.autocasting.application.proposal.service.internal.ProposalClaimService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.PROPOSAL_TYPE_CASTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProposalServiceImplTest {

    private static final String TOKEN = "token";

    @Mock
    private ProposalRepository proposalRepository;
    @Mock
    private ProposalTypeHandler castingHandler;
    @Mock
    private ProposalClaimService proposalClaimService;
    @Mock
    private AuthContext authContext;
    @Mock
    private UserRepository userRepository;

    private ProposalServiceImpl service;
    private UserEntity currentUser;

    @BeforeEach
    void setUp() {
        lenient().when(castingHandler.typeCode()).thenReturn(PROPOSAL_TYPE_CASTING);
        lenient().when(castingHandler.requirement()).thenReturn(ProposalRequirement.EMPLOYER_ONBOARDING_COMPLETED);
        service = new ProposalServiceImpl(
            proposalRepository,
            new ProposalTypeHandlerRegistry(List.of(castingHandler)),
            proposalClaimService,
            authContext,
            userRepository
        );
        currentUser = UserEntity.builder().id(UUID.randomUUID()).build();
        lenient().when(authContext.getCurrentUserOrThrow()).thenReturn(currentUser);
    }

    private ProposalEntity proposal(ProposalStatus status) {
        ProposalTypeOptionEntity type = new ProposalTypeOptionEntity();
        type.setStringCode(PROPOSAL_TYPE_CASTING);
        ProposalEntity proposal = ProposalEntity.builder()
            .id(UUID.randomUUID())
            .type(type)
            .token(TOKEN)
            .status(status)
            .companyNameHint("Productora X")
            .build();
        when(proposalRepository.findByTokenAndDeletedFalse(TOKEN)).thenReturn(Optional.of(proposal));
        return proposal;
    }

    private void assertGone(Runnable call) {
        ApiException ex = assertThrows(ApiException.class, call::run);
        assertEquals(HttpStatus.GONE, ex.getStatus());
    }

    @Test
    void getByToken_pending_returnsPreviewAndMarksFirstOpened() {
        ProposalEntity proposal = proposal(ProposalStatus.PENDING);
        Object preview = new Object();
        when(castingHandler.buildPreview(proposal)).thenReturn(preview);

        PublicProposalResponse response = service.getByToken(TOKEN);

        assertEquals(PROPOSAL_TYPE_CASTING, response.typeCode());
        assertEquals(ProposalRequirement.EMPLOYER_ONBOARDING_COMPLETED, response.requirement().code());
        assertEquals(UserMode.EMPLOYER, response.requirement().requiredMode());
        assertSame(preview, response.preview());
        assertNotNull(proposal.getFirstOpenedAt());
    }

    @Test
    void getByToken_alreadyOpened_keepsFirstOpenedAt() {
        ProposalEntity proposal = proposal(ProposalStatus.PENDING);
        LocalDateTime firstOpenedAt = LocalDateTime.now().minusDays(1);
        proposal.setFirstOpenedAt(firstOpenedAt);

        service.getByToken(TOKEN);

        assertEquals(firstOpenedAt, proposal.getFirstOpenedAt());
        verify(proposalRepository, never()).save(any());
    }

    @Test
    void getByToken_claimedOrRevokedOrUnknown_isGone() {
        proposal(ProposalStatus.CLAIMED);
        assertGone(() -> service.getByToken(TOKEN));

        proposal(ProposalStatus.REVOKED);
        assertGone(() -> service.getByToken(TOKEN));

        when(proposalRepository.findByTokenAndDeletedFalse("unknown")).thenReturn(Optional.empty());
        assertGone(() -> service.getByToken("unknown"));
    }

    @Test
    void attach_pending_recordsAttachmentAndSetsRequiredMode() {
        ProposalEntity proposal = proposal(ProposalStatus.PENDING);

        service.attach(TOKEN);

        verify(proposalRepository).attachUser(proposal.getId(), currentUser.getId());
        assertEquals(UserMode.EMPLOYER, currentUser.getActiveMode());
        verify(userRepository).save(currentUser);
    }

    @Test
    void attach_notPending_isGone() {
        proposal(ProposalStatus.CLAIMED);

        assertGone(() -> service.attach(TOKEN));
        verify(proposalRepository, never()).attachUser(any(), any());
    }

    @Test
    void claim_pending_claimsAndSwitchesToRequiredMode() {
        ProposalEntity proposal = proposal(ProposalStatus.PENDING);
        ProposalClaimResult result = new ProposalClaimResult(PROPOSAL_TYPE_CASTING, List.of(), Map.of("published", true));
        when(proposalClaimService.claimIfPending(proposal.getId(), currentUser)).thenReturn(Optional.of(result));

        assertSame(result, service.claim(TOKEN));
        assertEquals(UserMode.EMPLOYER, currentUser.getActiveMode());
        verify(userRepository).save(currentUser);
    }

    @Test
    void claim_alreadyClaimedMeanwhile_isGone() {
        ProposalEntity proposal = proposal(ProposalStatus.PENDING);
        when(proposalClaimService.claimIfPending(proposal.getId(), currentUser)).thenReturn(Optional.empty());

        assertGone(() -> service.claim(TOKEN));
    }

    @Test
    void getClaimResult_claimedByCurrentUser_describesClaim() {
        ProposalEntity proposal = proposal(ProposalStatus.CLAIMED);
        proposal.setClaimedByUser(currentUser);
        ProposalClaimResult result = new ProposalClaimResult(PROPOSAL_TYPE_CASTING, List.of(), Map.of("published", true));
        when(castingHandler.describeClaim(proposal)).thenReturn(result);

        assertSame(result, service.getClaimResult(TOKEN));
    }

    @Test
    void getClaimResult_claimedBySomeoneElse_isGone() {
        ProposalEntity proposal = proposal(ProposalStatus.CLAIMED);
        proposal.setClaimedByUser(UserEntity.builder().id(UUID.randomUUID()).build());

        assertGone(() -> service.getClaimResult(TOKEN));
    }

    @Test
    void getClaimResult_stillPending_isGone() {
        proposal(ProposalStatus.PENDING);

        assertGone(() -> service.getClaimResult(TOKEN));
    }
}
