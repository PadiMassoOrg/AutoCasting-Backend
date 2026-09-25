package com.padimasso.autocasting.application.proposal.type.casting.service.impl;

import com.padimasso.autocasting.application.admin.mapper.AdminCastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.service.internal.CastingDataApplier;
import com.padimasso.autocasting.application.castings.service.internal.CastingPublishability;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.proposal.dto.request.ProposalInternalReferenceRequest;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalProgress;
import com.padimasso.autocasting.application.proposal.model.ProposalStatus;
import com.padimasso.autocasting.application.proposal.repository.ProposalRepository;
import com.padimasso.autocasting.application.proposal.service.internal.ProposalTokenGenerator;
import com.padimasso.autocasting.application.proposal.type.casting.dto.request.CastingProposalCreateRequest;
import com.padimasso.autocasting.application.proposal.type.casting.dto.request.CastingProposalRoleRequest;
import com.padimasso.autocasting.application.proposal.type.casting.dto.response.CastingProposalCreatedResponse;
import com.padimasso.autocasting.application.proposal.type.casting.dto.response.CastingProposalDetailsResponse;
import com.padimasso.autocasting.application.proposal.type.casting.service.CastingProposalService;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import com.padimasso.autocasting.application.sitemetadata.model.ProposalTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.ProposalTypeOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final AdminCastingMapper adminCastingMapper;

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
            addNewRole(casting, roleRequest);
        }

        assertCompleteWithFutureDeadline(casting);
        CastingEntity savedCasting = castingRepository.save(casting);

        ProposalEntity proposal = ProposalEntity.builder()
            .type(castingType)
            .token(proposalTokenGenerator.generate())
            .status(ProposalStatus.PENDING)
            .casting(savedCasting)
            .build();
        applyInternalReference(proposal, request.internalReference());
        ProposalEntity savedProposal = proposalRepository.save(proposal);

        return new CastingProposalCreatedResponse(savedProposal.getId(), savedProposal.getToken());
    }

    @Override
    @Transactional
    public CastingProposalDetailsResponse getDetails(UUID proposalId) {
        ProposalEntity proposal = proposalRepository.findById(proposalId)
            .filter(found -> found.getCasting() != null)
            .orElseThrow(() -> new IllegalArgumentException(PROPOSALS_NOT_FOUND));
        return toDetailsResponse(proposal);
    }

    @Override
    @Transactional
    public CastingProposalDetailsResponse update(UUID proposalId, CastingProposalCreateRequest request) {
        ProposalEntity proposal = proposalRepository.findByIdForUpdate(proposalId)
            .filter(found -> found.getCasting() != null)
            .orElseThrow(() -> new IllegalArgumentException(PROPOSALS_NOT_FOUND));
        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new IllegalStateException(PROPOSALS_NOT_PENDING);
        }

        CastingEntity casting = proposal.getCasting();
        castingDataApplier.applyCastingData(casting, request.casting());

        Map<UUID, CastingRoleEntity> activeRolesById = activeRoles(casting).stream()
            .collect(Collectors.toMap(CastingRoleEntity::getId, Function.identity()));
        Set<UUID> keptRoleIds = new HashSet<>();

        for (CastingProposalRoleRequest roleRequest : request.roles()) {
            if (roleRequest.id() == null) {
                addNewRole(casting, roleRequest);
                continue;
            }
            CastingRoleEntity role = activeRolesById.get(roleRequest.id());
            if (role == null) {
                throw new IllegalArgumentException(CASTINGS_ROLE_MISMATCH);
            }
            castingDataApplier.applyRoleData(role, roleRequest.toCastingRoleRequest());
            keptRoleIds.add(role.getId());
        }
        activeRolesById.values().stream()
            .filter(role -> !keptRoleIds.contains(role.getId()))
            .forEach(role -> role.setDeleted(true));

        assertCompleteWithFutureDeadline(casting);
        castingRepository.save(casting);

        applyInternalReference(proposal, request.internalReference());
        return toDetailsResponse(proposalRepository.save(proposal));
    }

    private void addNewRole(CastingEntity casting, CastingProposalRoleRequest roleRequest) {
        CastingRoleEntity role = CastingRoleEntity.builder().casting(casting).build();
        castingDataApplier.applyRoleData(role, roleRequest.toCastingRoleRequest());
        casting.getRoles().add(role);
    }

    private void assertCompleteWithFutureDeadline(CastingEntity casting) {
        if (!CastingPublishability.isPublishable(casting)) {
            throw new IllegalArgumentException(PROPOSALS_CASTING_INCOMPLETE);
        }
        if (casting.getApplicationDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(PROPOSALS_CASTING_DEADLINE_PASSED);
        }
    }

    private void applyInternalReference(ProposalEntity proposal, ProposalInternalReferenceRequest reference) {
        proposal.setContactName(reference != null ? TextNormalizer.normalizeNullable(reference.contactName()) : null);
        proposal.setContactEmail(reference != null ? TextNormalizer.normalizeNullable(reference.contactEmail()) : null);
        proposal.setContactWhatsapp(reference != null ? TextNormalizer.normalizeNullable(reference.contactWhatsapp()) : null);
        proposal.setCompanyNameHint(reference != null ? TextNormalizer.normalizeNullable(reference.companyNameHint()) : null);
        proposal.setInternalNotes(reference != null ? TextNormalizer.normalizeNullable(reference.internalNotes()) : null);
    }

    private List<CastingRoleEntity> activeRoles(CastingEntity casting) {
        return casting.getRoles() == null
            ? List.of()
            : casting.getRoles().stream().filter(role -> role != null && !role.isDeleted()).toList();
    }

    private CastingProposalDetailsResponse toDetailsResponse(ProposalEntity proposal) {
        boolean hasAttachments = !proposalRepository.findAttachedProposalIds(List.of(proposal.getId())).isEmpty();
        CastingEntity casting = proposal.getCasting();

        return new CastingProposalDetailsResponse(
            proposal.getId(),
            proposal.getToken(),
            proposal.getStatus(),
            ProposalProgress.of(proposal.getStatus(), proposal.getFirstOpenedAt(), hasAttachments),
            proposal.getContactName(),
            proposal.getContactEmail(),
            proposal.getContactWhatsapp(),
            proposal.getCompanyNameHint(),
            proposal.getInternalNotes(),
            proposal.getFirstOpenedAt(),
            proposal.getCreatedAt(),
            proposal.getCreatedBy(),
            proposal.getModifiedAt(),
            adminCastingMapper.toDetailsResponse(casting),
            activeRoles(casting).stream().map(adminCastingMapper::toRoleResponse).toList()
        );
    }
}
