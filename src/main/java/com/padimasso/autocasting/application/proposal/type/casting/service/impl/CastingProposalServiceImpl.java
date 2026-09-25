package com.padimasso.autocasting.application.proposal.type.casting.service.impl;

import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.service.internal.CastingDataApplier;
import com.padimasso.autocasting.application.castings.service.internal.CastingPublishability;
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
import com.padimasso.autocasting.application.proposal.type.casting.service.CastingProposalService;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import com.padimasso.autocasting.application.sitemetadata.model.ProposalTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.ProposalTypeOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.padimasso.autocasting.config.AppConstants.*;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.*;

@Service
@RequiredArgsConstructor
public class CastingProposalServiceImpl implements CastingProposalService {

    private final EmployerProfileRepository employerProfileRepository;
    private final CastingRepository castingRepository;
    private final ProposalRepository proposalRepository;
    private final ProposalTypeOptionRepository proposalTypeOptionRepository;
    private final SiteMetadataResolver siteMetadataResolver;
    private final CastingDataApplier castingDataApplier;
    private final ProposalTokenGenerator proposalTokenGenerator;

    // All-or-nothing: the casting, its roles and the proposal are only persisted when the casting is
    // complete (publishable) with a future deadline. There are no server-side drafts for proposals.
    @Override
    @Transactional
    public CastingProposalCreatedResponse create(CastingProposalCreateRequest request) {
        EmployerProfileEntity systemOwner = employerProfileRepository.findById(PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID)
            .orElseThrow(() -> new IllegalStateException(PROPOSALS_SYSTEM_OWNER_MISSING));
        ProposalTypeOptionEntity castingType = proposalTypeOptionRepository.findByStringCode(PROPOSAL_TYPE_CASTING)
            .orElseThrow(() -> new IllegalStateException(PROPOSALS_TYPE_NOT_FOUND));

        CastingEntity casting = CastingEntity.builder()
            .employerProfile(systemOwner)
            .status(siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_DRAFT))
            .build();
        castingDataApplier.applyCastingData(casting, request.casting());

        for (CastingProposalRoleRequest roleRequest : request.roles()) {
            CastingRoleEntity role = CastingRoleEntity.builder().casting(casting).build();
            castingDataApplier.applyRoleData(role, roleRequest.toCastingRoleRequest());
            casting.getRoles().add(role);
        }

        if (!CastingPublishability.isPublishable(casting)) {
            throw new IllegalArgumentException(PROPOSALS_CASTING_INCOMPLETE);
        }
        if (casting.getApplicationDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(PROPOSALS_CASTING_DEADLINE_PASSED);
        }

        CastingEntity savedCasting = castingRepository.save(casting);

        ProposalInternalReferenceRequest reference = request.internalReference();
        ProposalEntity proposal = ProposalEntity.builder()
            .type(castingType)
            .token(proposalTokenGenerator.generate())
            .status(ProposalStatus.PENDING)
            .casting(savedCasting)
            .contactName(reference != null ? TextNormalizer.normalizeNullable(reference.contactName()) : null)
            .contactEmail(reference != null ? TextNormalizer.normalizeNullable(reference.contactEmail()) : null)
            .contactWhatsapp(reference != null ? TextNormalizer.normalizeNullable(reference.contactWhatsapp()) : null)
            .companyNameHint(reference != null ? TextNormalizer.normalizeNullable(reference.companyNameHint()) : null)
            .internalNotes(reference != null ? TextNormalizer.normalizeNullable(reference.internalNotes()) : null)
            .build();
        ProposalEntity savedProposal = proposalRepository.save(proposal);

        return new CastingProposalCreatedResponse(savedProposal.getId(), savedProposal.getToken(), savedCasting.getDefaultCode());
    }
}
