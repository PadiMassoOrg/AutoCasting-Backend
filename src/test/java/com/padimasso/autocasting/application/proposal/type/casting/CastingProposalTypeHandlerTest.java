package com.padimasso.autocasting.application.proposal.type.casting;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.castings.dto.response.PublicCastingResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.service.CastingMediaCleanupService;
import com.padimasso.autocasting.application.castings.service.internal.CastingStatusTransitionPolicy;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalAssociatedEntity;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalClaimResult;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingModalityOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.GenderOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.PayRateTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProjectTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.RoleTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.CASTING_MODALITY_AUTOCASTING;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static com.padimasso.autocasting.config.AppConstants.PAY_RATE_TYPE_UNPAID;
import static com.padimasso.autocasting.config.AppConstants.PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_DEADLINE_PASSED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CastingProposalTypeHandlerTest {

    @Mock
    private CastingRepository castingRepository;
    @Mock
    private CastingMediaCleanupService castingMediaCleanupService;
    @Mock
    private EmployerProfileRepository employerProfileRepository;
    @Mock
    private SiteMetadataResolver siteMetadataResolver;
    @Mock
    private HistoryService historyService;

    private CastingProposalTypeHandler handler;
    private CastingStatusOptionEntity draft;
    private CastingStatusOptionEntity published;
    private UserEntity claimant;
    private EmployerProfileEntity claimantProfile;

    @BeforeEach
    void setUp() {
        handler = new CastingProposalTypeHandler(
            castingRepository,
            castingMediaCleanupService,
            new CastingMapper(),
            new CastingStatusTransitionPolicy(),
            employerProfileRepository,
            siteMetadataResolver,
            historyService
        );
        draft = status(CASTING_STATUS_DRAFT);
        published = status(CASTING_STATUS_PUBLISHED);
        lenient().when(siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_PUBLISHED)).thenReturn(published);

        claimant = UserEntity.builder().id(UUID.randomUUID()).build();
        claimantProfile = EmployerProfileEntity.builder().id(UUID.randomUUID()).build();
        lenient().when(employerProfileRepository.findByUserId(claimant.getId())).thenReturn(Optional.of(claimantProfile));
    }

    private static CastingStatusOptionEntity status(String code) {
        CastingStatusOptionEntity status = new CastingStatusOptionEntity();
        status.setStringCode(code);
        return status;
    }

    private ProposalEntity proposalWithCasting(LocalDate deadline) {
        CastingModalityOptionEntity modality = new CastingModalityOptionEntity();
        modality.setStringCode(CASTING_MODALITY_AUTOCASTING);
        PayRateTypeOptionEntity unpaid = new PayRateTypeOptionEntity();
        unpaid.setStringCode(PAY_RATE_TYPE_UNPAID);

        CastingEntity casting = CastingEntity.builder()
            .id(UUID.randomUUID())
            .defaultCode("C-TEST0001")
            .employerProfile(EmployerProfileEntity.builder().id(PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID).build())
            .status(draft)
            .title("Casting")
            .projectType(new ProjectTypeOptionEntity())
            .castingModality(modality)
            .applicationDeadline(deadline)
            .hasWardrobeFitting(false)
            .shootingStartDate(LocalDate.now().plusDays(20))
            .shootingEndDate(LocalDate.now().plusDays(21))
            .roles(new HashSet<>())
            .build();
        casting.getRoles().add(CastingRoleEntity.builder()
            .casting(casting)
            .roleName("Protagonista")
            .roleType(new RoleTypeOptionEntity())
            .gender(new GenderOptionEntity())
            .ageMin((short) 20)
            .ageMax((short) 30)
            .payRateType(unpaid)
            .build());

        return ProposalEntity.builder().id(UUID.randomUUID()).casting(casting).companyNameHint("Productora X").build();
    }

    @Test
    void associate_futureDeadline_reassignsOwnerAndPublishes() {
        ProposalEntity proposal = proposalWithCasting(LocalDate.now().plusDays(10));

        handler.associate(proposal, claimant);

        CastingEntity casting = proposal.getCasting();
        assertSame(claimantProfile, casting.getEmployerProfile());
        assertSame(published, casting.getStatus());
        verify(castingRepository).save(casting);
        verify(historyService).createHistoryEntry(eq(EntityType.CASTING), eq(casting.getId()), anyString(), any());
    }

    @Test
    void associate_deadlinePassed_reassignsOwnerButStaysDraft() {
        ProposalEntity proposal = proposalWithCasting(LocalDate.now().minusDays(1));

        handler.associate(proposal, claimant);

        assertSame(claimantProfile, proposal.getCasting().getEmployerProfile());
        assertSame(draft, proposal.getCasting().getStatus());
    }

    @Test
    void describeClaim_published_reportsPublishedCasting() {
        ProposalEntity proposal = proposalWithCasting(LocalDate.now().plusDays(10));
        handler.associate(proposal, claimant);

        ProposalClaimResult result = handler.describeClaim(proposal);

        assertEquals(true, result.outcome().get("published"));
        assertEquals(EntityType.CASTING, result.associated().getFirst().entityType());
        assertEquals("C-TEST0001", result.associated().getFirst().slug());
    }

    @Test
    void describeClaim_deadlinePassed_reportsDraftWithReason() {
        ProposalEntity proposal = proposalWithCasting(LocalDate.now().minusDays(1));
        handler.associate(proposal, claimant);

        ProposalClaimResult result = handler.describeClaim(proposal);

        assertEquals(false, result.outcome().get("published"));
        assertEquals(CASTINGS_DEADLINE_PASSED, result.outcome().get("reason"));
    }

    @Test
    void buildPreview_neverExposesInternalReference() {
        ProposalEntity proposal = proposalWithCasting(LocalDate.now().plusDays(10));

        PublicCastingResponse preview = (PublicCastingResponse) handler.buildPreview(proposal);

        assertNull(preview.employerInfo().companyName());
        assertEquals(1, preview.roles().size());
    }

    @Test
    void associatedEntities_returnsTheCasting() {
        ProposalEntity proposal = proposalWithCasting(LocalDate.now().plusDays(10));

        List<ProposalAssociatedEntity> associated = handler.associatedEntities(proposal);

        assertEquals(
            List.of(new ProposalAssociatedEntity(EntityType.CASTING, proposal.getCasting().getId(), proposal.getCasting().getDefaultCode())),
            associated
        );
    }

    @Test
    void discard_softDeletesCastingAndCleansItsMediaFolder() {
        ProposalEntity proposal = proposalWithCasting(LocalDate.now().plusDays(10));

        handler.discard(proposal);

        verify(castingRepository).softDelete(proposal.getCasting());
        verify(castingMediaCleanupService).deleteCastingFolder(PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID, proposal.getCasting().getId());
    }

    @Test
    void discard_alreadyDeletedCasting_doesNothing() {
        ProposalEntity proposal = proposalWithCasting(LocalDate.now().plusDays(10));
        proposal.getCasting().setDeleted(true);

        handler.discard(proposal);

        verify(castingRepository, never()).softDelete(any());
        verify(castingMediaCleanupService, never()).deleteCastingFolder(any(), any());
    }
}
