package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.applications.repository.CastingApplicationRepository;
import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.context.EmployerContext;
import com.padimasso.autocasting.application.auth.dto.response.EmployerPrincipal;
import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingUpsertRequest;
import com.padimasso.autocasting.application.castings.dto.response.*;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingCardResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.order.EmployerCastingsOrderBy;
import com.padimasso.autocasting.application.castings.repository.specification.CastingSpecs;
import com.padimasso.autocasting.application.castings.service.CastingService;
import com.padimasso.autocasting.application.castings.service.internal.CastingStatusTransitionPolicy;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.*;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.*;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingServiceImpl implements CastingService {
    private static final String DEFAULT_EMPTY_CASTING_TITLE = "Sin titulo";

    private final AuthContext authContext;
    private final TalentProfileRepository talentProfileRepository;
    private final EmployerContext employerContext;
    private final CastingRepository castingRepository;
    private final SiteMetadataResolver siteMetadataResolver;
    private final CastingStatusTransitionPolicy castingStatusTransitionPolicy;
    private final CastingApplicationRepository castingApplicationRepository;
    private final CastingMapper castingMapper;

    @Override
    @Transactional
    public String createEmptyCasting() {
        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();

        CastingStatusOptionEntity draftStatus =
            siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_DRAFT);

        CastingEntity casting = CastingEntity.builder()
            .employerProfile(employer.employerProfile())
            .status(draftStatus)
            .title(DEFAULT_EMPTY_CASTING_TITLE)
            .castingModality(siteMetadataResolver.resolveCastingModalityByCodeOrThrow(CASTING_MODALITY_AUTOCASTING))
            .hasWardrobeFitting(Boolean.FALSE)
            .build();

        CastingEntity saved = castingRepository.save(casting);
        return saved.getDefaultCode();
    }

    @Override
    @Transactional
    public CastingResponse createCasting(CastingUpsertRequest request) {
        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();

        CastingStatusOptionEntity draftStatus =
            siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_DRAFT);

        CastingEntity casting = CastingEntity.builder()
            .employerProfile(employer.employerProfile())
            .status(draftStatus)
            .build();

        applyCastingData(casting, request);

        CastingEntity saved = castingRepository.save(casting);
        return toCastingResponse(saved);
    }

    @Override
    @Transactional
    public CastingResponse updateCasting(UUID castingId, CastingUpsertRequest request) {
        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();

        CastingEntity casting = castingRepository.findByIdAndEmployerProfile_IdAndDeletedFalse(castingId, employer.employerProfile().getId())
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));
        assertDraftEditable(casting);

        applyCastingData(casting, request);

        CastingEntity saved = castingRepository.save(casting);
        return toCastingResponse(saved);
    }

    @Override
    public EmployerCastingEditorResponse getCastingEditorBySlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException(GENERAL_SLUG_REQUIRED);
        }

        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();
        UUID employerProfileId = employer.employerProfile().getId();

        CastingEntity casting = castingRepository.findByDefaultCodeAndEmployerProfile_IdAndDeletedFalse(slug.trim(), employerProfileId)
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));

        return castingMapper.toEmployerCastingEditorResponse(casting, isPublishable(casting));
    }

    @Override
    public CastingResponse getEmployerCastingDetailsBySlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException(GENERAL_SLUG_REQUIRED);
        }

        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();
        UUID employerProfileId = employer.employerProfile().getId();

        CastingEntity casting = castingRepository.findByDefaultCodeAndEmployerProfile_IdAndDeletedFalse(slug.trim(), employerProfileId)
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));

        return toCastingResponse(casting);
    }

    @Override
    public EmployerCastingCheckoutSummaryResponse getEmployerCastingCheckoutSummary(UUID castingId) {
        if (castingId == null) {
            throw new IllegalArgumentException(CASTING_ID_REQUIRED);
        }

        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();

        CastingEntity casting = castingRepository.findByIdAndEmployerProfile_IdAndDeletedFalse(castingId, employer.employerProfile().getId())
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));

        return castingMapper.toEmployerCastingCheckoutSummaryResponse(casting);
    }

    @Override
    @Transactional
    public List<CastingCardResponse> getMyCastings(EmployerCastingsFilter incomingFilter, int page, int size) {
        var employer = employerContext.getCurrentEmployerOrThrow();
        var employerProfileId = employer.employerProfile().getId();

        var orderBy = incomingFilter.orderBy() != null
            ? incomingFilter.orderBy()
            : EmployerCastingsOrderBy.CREATION_DATE_DESC;

        var effectiveFilter = new EmployerCastingsFilter(
            employerProfileId,
            incomingFilter.search(),
            incomingFilter.projectTypeIdTokens(),
            incomingFilter.statusIdTokens(),
            orderBy
        );

        var spec = CastingSpecs.fromFilter(effectiveFilter);

        int ps = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        var pageable = orderBy.isDeadlineOrder()
            ? PageRequest.of(page, ps)
            : PageRequest.of(page, ps, orderBy.toSort());

        if (orderBy == EmployerCastingsOrderBy.DEADLINE_ASC) {
            spec = spec.and(CastingSpecs.orderByDeadlineAscNullsLast());
        } else if (orderBy == EmployerCastingsOrderBy.DEADLINE_DESC) {
            spec = spec.and(CastingSpecs.orderByDeadlineDescNullsLast());
        }

        return castingRepository.findAll(spec, pageable).getContent().stream()
            .map(casting -> castingMapper.toCardResponse(
                casting,
                castingStatusTransitionPolicy.allowedNextStatuses(
                    casting.getStatus() != null ? casting.getStatus().getStringCode() : null,
                    casting.getApplicationDeadline(),
                    isPublishable(casting)
                )
            ))
            .toList();
    }

    @Override
    @Transactional
    public void deleteCasting(UUID castingId) {
        CastingEntity casting = castingRepository.findByIdAndDeletedFalse(castingId)
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));
        castingRepository.softDelete(casting);
    }

    @Override
    @Transactional
    public EmployerCastingEditorResponse publishCasting(UUID castingId) {
        return transitionCasting(castingId, CASTING_STATUS_PUBLISHED);
    }

    @Override
    @Transactional
    public EmployerCastingEditorResponse setDraftCasting(UUID castingId) {
        return transitionCasting(castingId, CASTING_STATUS_DRAFT);
    }

    @Override
    @Transactional
    public EmployerCastingEditorResponse pauseCasting(UUID castingId) {
        return transitionCasting(castingId, CASTING_STATUS_PAUSED);
    }

    @Override
    @Transactional
    public EmployerCastingEditorResponse closeCasting(UUID castingId) {
        return transitionCasting(castingId, CASTING_STATUS_CLOSED);
    }

    @Override
    @Transactional
    public EmployerCastingEditorResponse archiveCasting(UUID castingId) {
        return transitionCasting(castingId, CASTING_STATUS_ARCHIVED);
    }

    @Override
    public PublicCastingDetailsResponse getPublicCastingDetailsBySlugAndRoleId(String slug, UUID roleId) {
        if (slug == null || slug.isBlank()) throw new IllegalArgumentException(GENERAL_SLUG_REQUIRED);
        if (roleId == null) throw new IllegalArgumentException(GENERAL_ROLE_ID_REQUIRED);

        CastingEntity casting = castingRepository.findByDefaultCodeAndDeletedFalse(slug.trim())
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));

        String statusCode = casting.getStatus() != null ? casting.getStatus().getStringCode() : null;
        if (!List.of(CASTING_STATUS_PUBLISHED, CASTING_STATUS_CLOSED).contains(statusCode)) {
            throw new IllegalArgumentException(CASTINGS_NOT_FOUND);
        }

        PublicCastingResponse response = toPublicCastingResponse(casting);
        List<PublicCastingRoleResponse> filteredRoles = response.roles().stream()
            .filter(role -> roleId.equals(role.id()))
            .toList();

        if (filteredRoles.isEmpty()) {
            throw new IllegalArgumentException(CASTINGS_NOT_FOUND);
        }

        PublicCastingResponse roleScoped = new PublicCastingResponse(
            response.employerInfo(),
            response.slug(),
            response.title(),
            response.projectType(),
            response.castingModality(),
            response.locationText(),
            response.applicationDeadline(),
            response.wardrobeFittingText(),
            response.shootingStartDate(),
            response.shootingEndDate(),
            response.description(),
            filteredRoles
        );

        boolean alreadyApplied = authContext.getCurrentUserOptional()
            .flatMap(user -> talentProfileRepository.findByUserId(user.getId()))
            .map(profile -> castingApplicationRepository.existsByTalentProfileIdAndRoleId(profile.getId(), roleId))
            .orElse(false);

        return new PublicCastingDetailsResponse(roleScoped, alreadyApplied);
    }

    @Override
    public PublicCastingOverviewResponse getPublicCastingDetailsBySlug(String slug) {
        if (slug == null || slug.isBlank()) throw new IllegalArgumentException(GENERAL_SLUG_REQUIRED);

        CastingEntity casting = castingRepository.findByDefaultCodeAndDeletedFalse(slug.trim())
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));

        String statusCode = casting.getStatus() != null ? casting.getStatus().getStringCode() : null;
        if (!List.of(CASTING_STATUS_PUBLISHED, CASTING_STATUS_CLOSED).contains(statusCode)) {
            throw new IllegalArgumentException(CASTINGS_NOT_FOUND);
        }

        List<UUID> appliedRoleIds = authContext.getCurrentUserOptional()
            .flatMap(user -> talentProfileRepository.findByUserId(user.getId()))
            .map(profile -> castingApplicationRepository.findAppliedRoleIdsByTalentProfileIdAndCastingId(profile.getId(), casting.getId()))
            .orElse(List.of());

        return new PublicCastingOverviewResponse(toPublicCastingResponse(casting), appliedRoleIds);
    }

    private PublicCastingResponse toPublicCastingResponse(CastingEntity casting) {
        UUID employerProfileId = casting.getEmployerProfile() != null ? casting.getEmployerProfile().getId() : null;
        Long totalCastings = employerProfileId != null
            ? castingRepository.countByEmployerProfile_IdAndDeletedFalseAndStatus_StringCodeIn(
                employerProfileId,
                List.of(CASTING_STATUS_PUBLISHED, CASTING_STATUS_CLOSED)
            )
            : null;
        LocalDate memberSince = casting.getEmployerProfile() != null && casting.getEmployerProfile().getCreatedAt() != null
            ? casting.getEmployerProfile().getCreatedAt().toLocalDate()
            : null;

        return castingMapper.toPublicCastingResponse(
            casting,
            castingMapper.toPublicCastingEmployerInfoResponse(casting.getEmployerProfile(), totalCastings, memberSince)
        );
    }

    private EmployerCastingEditorResponse transitionCasting(UUID castingId, String targetStatusCode) {
        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();

        CastingEntity casting = castingRepository.findByIdAndEmployerProfile_IdAndDeletedFalse(castingId, employer.employerProfile().getId())
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));

        String currentStatusCode = casting.getStatus() != null ? casting.getStatus().getStringCode() : null;
        boolean publishable = isPublishable(casting);

        switch (targetStatusCode) {
            case CASTING_STATUS_PUBLISHED -> castingStatusTransitionPolicy.assertCanPublish(currentStatusCode, casting.getApplicationDeadline(), publishable);
            case CASTING_STATUS_DRAFT -> castingStatusTransitionPolicy.assertCanSetDraft(currentStatusCode);
            case CASTING_STATUS_PAUSED -> castingStatusTransitionPolicy.assertCanPause(currentStatusCode);
            case CASTING_STATUS_CLOSED -> castingStatusTransitionPolicy.assertCanClose(currentStatusCode);
            case CASTING_STATUS_ARCHIVED -> castingStatusTransitionPolicy.assertCanArchive(currentStatusCode);
            default -> throw new IllegalStateException(CASTINGS_INVALID_STATUS_TRANSITION);
        }

        casting.setStatus(siteMetadataResolver.resolveCastingStatusByCodeOrThrow(targetStatusCode));
        castingRepository.save(casting);

        return castingMapper.toEmployerCastingEditorResponse(casting, publishable);
    }

    private CastingResponse toCastingResponse(CastingEntity casting) {
        UUID employerProfileId = casting.getEmployerProfile() != null ? casting.getEmployerProfile().getId() : null;
        Long totalCastings = employerProfileId != null
            ? castingRepository.countByEmployerProfile_IdAndDeletedFalseAndStatus_StringCodeIn(
                employerProfileId,
                List.of(CASTING_STATUS_PUBLISHED, CASTING_STATUS_CLOSED)
            )
            : null;
        LocalDate memberSince = casting.getEmployerProfile() != null && casting.getEmployerProfile().getCreatedAt() != null
            ? casting.getEmployerProfile().getCreatedAt().toLocalDate()
            : null;

        return castingMapper.toCastingResponse(
            casting,
            castingMapper.toCastingEmployerInfoResponse(casting.getEmployerProfile(), totalCastings, memberSince),
            isPublishable(casting)
        );
    }

    private void applyCastingData(CastingEntity casting, CastingUpsertRequest request) {
        casting.setTitle(TextNormalizer.normalizeNullable(request.title()));
        casting.setProjectType(request.projectTypeId() != null
            ? siteMetadataResolver.resolveProjectTypeOrThrow(request.projectTypeId())
            : null);
        casting.setCastingModality(request.castingModalityId() != null
            ? siteMetadataResolver.resolveCastingModalityOrThrow(request.castingModalityId())
            : null);
        casting.setLocationText(TextNormalizer.normalizeNullable(request.locationText()));
        casting.setApplicationDeadline(request.applicationDeadline());
        casting.setHasWardrobeFitting(request.hasWardrobeFitting());
        casting.setWardrobeFittingText(Boolean.FALSE.equals(request.hasWardrobeFitting())
            ? null
            : TextNormalizer.normalizeNullable(request.wardrobeFittingText()));
        casting.setShootingStartDate(request.shootingStartDate());
        casting.setShootingEndDate(request.shootingEndDate());
        casting.setDescription(TextNormalizer.normalizeNullable(request.description()));
    }

    private boolean isPublishable(CastingEntity casting) {
        if (!hasCompleteBasicInfo(casting)) return false;
        List<CastingRoleEntity> activeRoles = casting.getRoles() == null
            ? List.of()
            : casting.getRoles().stream().filter(role -> role != null && !role.isDeleted()).toList();
        if (activeRoles.isEmpty()) return false;
        return activeRoles.stream().allMatch(this::hasCompleteRole);
    }

    private boolean hasCompleteBasicInfo(CastingEntity casting) {
        if (casting == null) return false;
        if (casting.getTitle() == null || casting.getProjectType() == null || casting.getCastingModality() == null) return false;
        if (casting.getApplicationDeadline() == null || casting.getHasWardrobeFitting() == null) return false;
        if (casting.getShootingStartDate() == null || casting.getShootingEndDate() == null) return false;
        if (CASTING_MODALITY_ON_SITE.equals(casting.getCastingModality().getStringCode()) && casting.getLocationText() == null) return false;
        return !Boolean.TRUE.equals(casting.getHasWardrobeFitting()) || casting.getWardrobeFittingText() != null;
    }

    private boolean hasCompleteRole(CastingRoleEntity role) {
        if (role == null) return false;
        if (role.getRoleName() == null || role.getRoleType() == null || role.getGender() == null) return false;
        if (role.getAgeMin() == null || role.getAgeMax() == null || role.getAgeMin() > role.getAgeMax()) return false;
        if (role.getPayRateType() == null) return false;

        String payRateCode = role.getPayRateType().getStringCode();
        boolean isUnpaidLike = payRateCode != null && (
            payRateCode.endsWith(".unpaid")
                || payRateCode.endsWith(".cooperative")
                || payRateCode.endsWith(".collaborative")
        );

        if (isUnpaidLike) {
            return true;
        }

        return role.getCurrency() != null && role.getAmount() != null && role.getAmount().signum() > 0;
    }

    private void assertDraftEditable(CastingEntity casting) {
        String statusCode = casting != null && casting.getStatus() != null ? casting.getStatus().getStringCode() : null;
        if (!CASTING_STATUS_DRAFT.equals(statusCode)) {
            throw new IllegalStateException(CASTINGS_ONLY_DRAFT_EDITABLE);
        }
    }
}
