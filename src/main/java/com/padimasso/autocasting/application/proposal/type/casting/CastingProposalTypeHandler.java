package com.padimasso.autocasting.application.proposal.type.casting;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.castings.dto.response.PublicCastingEmployerInfoResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.service.CastingMediaCleanupService;
import com.padimasso.autocasting.application.castings.service.internal.CastingPublishability;
import com.padimasso.autocasting.application.castings.service.internal.CastingStatusTransitionPolicy;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalAssociatedEntity;
import com.padimasso.autocasting.application.proposal.dto.response.ProposalClaimResult;
import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalRequirement;
import com.padimasso.autocasting.application.proposal.type.ProposalTypeHandler;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static com.padimasso.autocasting.config.AppConstants.PROPOSAL_TYPE_CASTING;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROPOSALS_REQUIREMENT_NOT_MET;

@Component
@RequiredArgsConstructor
public class CastingProposalTypeHandler implements ProposalTypeHandler {

    private final CastingRepository castingRepository;
    private final CastingMediaCleanupService castingMediaCleanupService;
    private final CastingMapper castingMapper;
    private final CastingStatusTransitionPolicy castingStatusTransitionPolicy;
    private final EmployerProfileRepository employerProfileRepository;
    private final SiteMetadataResolver siteMetadataResolver;
    private final HistoryService historyService;

    @Override
    public String typeCode() {
        return PROPOSAL_TYPE_CASTING;
    }

    @Override
    public ProposalRequirement requirement() {
        return ProposalRequirement.EMPLOYER_ONBOARDING_COMPLETED;
    }

    @Override
    public Object buildPreview(ProposalEntity proposal) {
        PublicCastingEmployerInfoResponse employerInfo = new PublicCastingEmployerInfoResponse(
            null, null, null, null, null, null, null, null
        );
        return castingMapper.toPublicCastingResponse(proposal.getCasting(), employerInfo);
    }

    @Override
    public void associate(ProposalEntity proposal, UserEntity user) {
        CastingEntity casting = proposal.getCasting();
        EmployerProfileEntity employerProfile = employerProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> ApiException.conflict(PROPOSALS_REQUIREMENT_NOT_MET));

        casting.setEmployerProfile(employerProfile);
        boolean published = unpublishableReason(casting) == null;
        if (published) {
            casting.setStatus(siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_PUBLISHED));
        }
        castingRepository.save(casting);

        historyService.createHistoryEntry(
            EntityType.CASTING,
            casting.getId(),
            "Claimed from proposal",
            Map.of("proposalId", proposal.getId(), "employerProfileId", employerProfile.getId(), "published", published)
        );
    }

    @Override
    public ProposalClaimResult describeClaim(ProposalEntity proposal) {
        CastingEntity casting = proposal.getCasting();
        String statusCode = casting.getStatus() != null ? casting.getStatus().getStringCode() : null;
        boolean published = CASTING_STATUS_PUBLISHED.equals(statusCode);

        Map<String, Object> outcome = new LinkedHashMap<>();
        outcome.put("published", published);
        if (!published) {
            outcome.put("reason", unpublishableReason(casting));
        }

        return new ProposalClaimResult(typeCode(), associatedEntities(proposal), outcome);
    }

    @Override
    public List<ProposalAssociatedEntity> associatedEntities(ProposalEntity proposal) {
        CastingEntity casting = proposal.getCasting();
        if (casting == null) return List.of();
        return List.of(new ProposalAssociatedEntity(EntityType.CASTING, casting.getId(), casting.getDefaultCode()));
    }

    @Override
    public void discard(ProposalEntity proposal) {
        CastingEntity casting = proposal.getCasting();
        if (casting == null || casting.isDeleted()) return;

        castingRepository.softDelete(casting);
        if (casting.getEmployerProfile() != null) {
            castingMediaCleanupService.deleteCastingFolder(casting.getEmployerProfile().getId(), casting.getId());
        }
    }

    private String unpublishableReason(CastingEntity casting) {
        try {
            castingStatusTransitionPolicy.assertCanPublish(
                CASTING_STATUS_DRAFT,
                casting.getApplicationDeadline(),
                CastingPublishability.isPublishable(casting)
            );
            return null;
        } catch (IllegalStateException ex) {
            return ex.getMessage();
        }
    }
}
