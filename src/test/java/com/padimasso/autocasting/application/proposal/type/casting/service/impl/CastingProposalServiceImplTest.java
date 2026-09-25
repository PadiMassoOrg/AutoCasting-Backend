package com.padimasso.autocasting.application.proposal.type.casting.service.impl;

import com.padimasso.autocasting.application.castings.dto.request.CastingUpsertRequest;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.service.internal.CastingDataApplier;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.proposal.dto.request.ProposalInternalReferenceRequest;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalStatus;
import com.padimasso.autocasting.application.proposal.repository.ProposalRepository;
import com.padimasso.autocasting.application.proposal.service.internal.ProposalTokenGenerator;
import com.padimasso.autocasting.application.proposal.type.casting.dto.request.CastingProposalCreateRequest;
import com.padimasso.autocasting.application.proposal.type.casting.dto.request.CastingProposalRoleRequest;
import com.padimasso.autocasting.application.proposal.type.casting.dto.response.CastingProposalCreatedResponse;
import com.padimasso.autocasting.application.sitemetadata.model.CastingModalityOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CurrencyOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.GenderOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.PayRateTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProjectTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProposalTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.RoleTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.ProposalTypeOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.*;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROPOSALS_CASTING_DEADLINE_PASSED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROPOSALS_CASTING_INCOMPLETE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CastingProposalServiceImplTest {

    @Mock
    private EmployerProfileRepository employerProfileRepository;
    @Mock
    private CastingRepository castingRepository;
    @Mock
    private ProposalRepository proposalRepository;
    @Mock
    private ProposalTypeOptionRepository proposalTypeOptionRepository;
    @Mock
    private SiteMetadataResolver siteMetadataResolver;
    @Mock
    private ProposalTokenGenerator proposalTokenGenerator;

    private CastingProposalServiceImpl service;
    private EmployerProfileEntity systemOwner;
    private ProposalTypeOptionEntity castingType;

    private final UUID projectTypeId = UUID.randomUUID();
    private final UUID modalityId = UUID.randomUUID();
    private final UUID roleTypeId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new CastingProposalServiceImpl(
            employerProfileRepository,
            castingRepository,
            proposalRepository,
            proposalTypeOptionRepository,
            siteMetadataResolver,
            new CastingDataApplier(siteMetadataResolver),
            proposalTokenGenerator
        );

        systemOwner = EmployerProfileEntity.builder().id(PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID).build();
        castingType = new ProposalTypeOptionEntity();
        castingType.setStringCode(PROPOSAL_TYPE_CASTING);

        CastingStatusOptionEntity draft = new CastingStatusOptionEntity();
        draft.setStringCode(CASTING_STATUS_DRAFT);
        CastingModalityOptionEntity autocasting = new CastingModalityOptionEntity();
        autocasting.setStringCode(CASTING_MODALITY_AUTOCASTING);
        PayRateTypeOptionEntity unpaid = new PayRateTypeOptionEntity();
        unpaid.setStringCode(PAY_RATE_TYPE_UNPAID);

        lenient().when(employerProfileRepository.findById(PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID)).thenReturn(Optional.of(systemOwner));
        lenient().when(proposalTypeOptionRepository.findByStringCode(PROPOSAL_TYPE_CASTING)).thenReturn(Optional.of(castingType));
        lenient().when(siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_DRAFT)).thenReturn(draft);
        lenient().when(siteMetadataResolver.resolveProjectTypeOrThrow(projectTypeId)).thenReturn(new ProjectTypeOptionEntity());
        lenient().when(siteMetadataResolver.resolveCastingModalityOrThrow(modalityId)).thenReturn(autocasting);
        lenient().when(siteMetadataResolver.resolveRoleTypeOrThrow(roleTypeId)).thenReturn(new RoleTypeOptionEntity());
        lenient().when(siteMetadataResolver.resolveGenderByCodeOrThrow(GENDER_OPTION_INDISTINCT)).thenReturn(new GenderOptionEntity());
        lenient().when(siteMetadataResolver.resolveProfessionsOrThrow(Set.of())).thenReturn(Set.of());
        lenient().when(siteMetadataResolver.resolveSkillsOrThrow(Set.of())).thenReturn(Set.of());
        lenient().when(siteMetadataResolver.resolvePayRateTypeByCodeOrThrow(PAY_RATE_TYPE_UNPAID)).thenReturn(unpaid);
        lenient().when(siteMetadataResolver.resolveCurrencyByCodeOrThrow(CURRENCY_ARS)).thenReturn(new CurrencyOptionEntity());
        lenient().when(proposalTokenGenerator.generate()).thenReturn("generated-token");
        lenient().when(castingRepository.save(any(CastingEntity.class))).thenAnswer(invocation -> {
            CastingEntity casting = invocation.getArgument(0);
            casting.setDefaultCode("C-TEST0001");
            return casting;
        });
        lenient().when(proposalRepository.save(any(ProposalEntity.class))).thenAnswer(invocation -> {
            ProposalEntity proposal = invocation.getArgument(0);
            proposal.setId(UUID.randomUUID());
            return proposal;
        });
    }

    private CastingUpsertRequest casting(String title, LocalDate deadline) {
        return new CastingUpsertRequest(
            title, projectTypeId, modalityId, null, deadline, false, null,
            LocalDate.now().plusDays(20), LocalDate.now().plusDays(21), "Descripción"
        );
    }

    private CastingProposalRoleRequest role() {
        return new CastingProposalRoleRequest(
            "Protagonista", roleTypeId, null, (short) 20, (short) 30, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null
        );
    }

    private CastingProposalCreateRequest request(CastingUpsertRequest casting, List<CastingProposalRoleRequest> roles) {
        return new CastingProposalCreateRequest(
            casting,
            roles,
            new ProposalInternalReferenceRequest("  Ana Pérez ", "ana@test.com", null, "Productora X", null)
        );
    }

    @Test
    void create_completeCasting_savesDraftCastingOwnedBySystemAndPendingProposal() {
        CastingProposalCreatedResponse response = service.create(
            request(casting("Casting", LocalDate.now().plusDays(10)), List.of(role(), role()))
        );

        ArgumentCaptor<CastingEntity> castingCaptor = ArgumentCaptor.forClass(CastingEntity.class);
        verify(castingRepository).save(castingCaptor.capture());
        CastingEntity savedCasting = castingCaptor.getValue();
        assertSame(systemOwner, savedCasting.getEmployerProfile());
        assertEquals(CASTING_STATUS_DRAFT, savedCasting.getStatus().getStringCode());
        assertEquals(2, savedCasting.getRoles().size());

        ArgumentCaptor<ProposalEntity> proposalCaptor = ArgumentCaptor.forClass(ProposalEntity.class);
        verify(proposalRepository).save(proposalCaptor.capture());
        ProposalEntity savedProposal = proposalCaptor.getValue();
        assertSame(castingType, savedProposal.getType());
        assertSame(savedCasting, savedProposal.getCasting());
        assertEquals(ProposalStatus.PENDING, savedProposal.getStatus());
        assertEquals("Ana Pérez", savedProposal.getContactName());

        assertEquals("generated-token", response.token());
        assertEquals("C-TEST0001", response.castingSlug());
    }

    @Test
    void create_incompleteCasting_throwsAndSavesNothing() {
        CastingUpsertRequest missingDeadline = casting("Casting", null);

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.create(request(missingDeadline, List.of(role())))
        );

        assertEquals(PROPOSALS_CASTING_INCOMPLETE, ex.getMessage());
        verify(castingRepository, never()).save(any());
        verify(proposalRepository, never()).save(any());
    }

    @Test
    void create_noRoles_throwsIncompleteAndSavesNothing() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.create(request(casting("Casting", LocalDate.now().plusDays(10)), List.of()))
        );

        assertEquals(PROPOSALS_CASTING_INCOMPLETE, ex.getMessage());
        verify(castingRepository, never()).save(any());
    }

    @Test
    void create_pastDeadline_throwsAndSavesNothing() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.create(request(casting("Casting", LocalDate.now().minusDays(1)), List.of(role())))
        );

        assertEquals(PROPOSALS_CASTING_DEADLINE_PASSED, ex.getMessage());
        verify(castingRepository, never()).save(any());
        verify(proposalRepository, never()).save(any());
    }

    @Test
    void create_withoutInternalReference_savesProposalWithEmptyReference() {
        service.create(new CastingProposalCreateRequest(casting("Casting", LocalDate.now().plusDays(10)), List.of(role()), null));

        ArgumentCaptor<ProposalEntity> proposalCaptor = ArgumentCaptor.forClass(ProposalEntity.class);
        verify(proposalRepository).save(proposalCaptor.capture());
        assertNull(proposalCaptor.getValue().getContactName());
    }
}
