package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.auth.context.EmployerContext;
import com.padimasso.autocasting.application.auth.dto.response.EmployerPrincipal;
import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.dto.response.CastingEmployerInfoResponse;
import com.padimasso.autocasting.application.castings.dto.response.CastingResponse;
import com.padimasso.autocasting.application.castings.dto.response.EmployerCastingEditorResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRequirementsSectionResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.*;
import com.padimasso.autocasting.application.castings.repository.*;
import com.padimasso.autocasting.application.castings.repository.order.EmployerCastingsOrderBy;
import com.padimasso.autocasting.application.castings.repository.projection.CastingCardStatusGateProjection;
import com.padimasso.autocasting.application.castings.repository.specification.CastingSpecs;
import com.padimasso.autocasting.application.castings.service.CastingService;
import com.padimasso.autocasting.application.castings.service.internal.CastingStatusTransitionPolicy;
import com.padimasso.autocasting.application.sitemetadata.model.CastingCompensationTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingSectionStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingCompensationTypeOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingSectionStatusOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingStatusOptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper.mapToSiteMetadataObject;
import static com.padimasso.autocasting.config.AppConstants.*;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingServiceImpl implements CastingService {

    private static final String CASTING_NOT_FOUND = "castings.not_found";
    private static final String CASTING_SECTION_NOT_FOUND = "castings.section.not_found";

    private final EmployerContext employerContext;
    private final CastingRepository castingRepository;
    private final CastingBasicInfoRepository castingBasicInfoRepository;
    private final CastingRolesSectionRepository castingRolesSectionRepository;
    private final CastingRequirementsSectionRepository castingActingRepository;
    private final CastingRemunerationsSectionRepository castingRemunerationsSectionRepository;
    private final CastingStatusOptionRepository castingStatusOptionRepository;
    private final CastingSectionStatusOptionRepository castingSectionStatusOptionRepository;
    private final CastingCompensationTypeOptionRepository castingCompensationTypeOptionRepository;
    private final CastingStatusTransitionPolicy castingStatusTransitionPolicy;
    private final CastingMapper castingMapper;

    @Override
    @Transactional
    public String createEmptyCasting() {
        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();

        CastingStatusOptionEntity draftStatus = castingStatusOptionRepository
            .findByStringCode(CASTING_STATUS_DRAFT)
            .orElseThrow(() -> new IllegalStateException("sitemetadata.casting_status.not_found"));

        CastingSectionStatusOptionEntity notStartedStatus = castingSectionStatusOptionRepository
            .findByStringCode(CASTING_SECTION_STATUS_NOT_STARTED)
            .orElseThrow(() -> new IllegalStateException("sitemetadata.casting_section_status.not_found"));

        CastingSectionStatusOptionEntity inProgressStatus = castingSectionStatusOptionRepository
            .findByStringCode(CASTING_SECTION_STATUS_IN_PROGRESS)
            .orElseThrow(() -> new IllegalStateException("sitemetadata.casting_section_status.not_found"));

        CastingSectionStatusOptionEntity completedStatus = castingSectionStatusOptionRepository
            .findByStringCode(CASTING_SECTION_STATUS_COMPLETED)
            .orElseThrow(() -> new IllegalStateException("sitemetadata.casting_section_status.not_found"));

        CastingCompensationTypeOptionEntity compensationPaid = castingCompensationTypeOptionRepository
            .findByStringCode(CASTING_COMPENSATION_TYPE_PAID)
            .orElseThrow(() -> new IllegalStateException("sitemetadata.casting_compensation_type.not_found"));

        CastingEntity casting = CastingEntity.builder()
            .employerProfile(employer.employerProfile())
            .status(draftStatus)
            .build();
        casting = castingRepository.save(casting);

        CastingBasicInfoEntity basicInfo = CastingBasicInfoEntity.builder()
            .casting(casting)
            .sectionStatus(notStartedStatus)
            .hasWardrobeFitting(false)
            .build();
        castingBasicInfoRepository.save(basicInfo);

        CastingRolesSectionEntity rolesSection = CastingRolesSectionEntity.builder()
            .casting(casting)
            .sectionStatus(inProgressStatus)
            .build();
        castingRolesSectionRepository.save(rolesSection);

        // Status Completed: Since there is no need for evaluation.
        // 1. Requirements have not null fields on creation.
        // 2. Not mandatory to have requirements in Section.
        CastingRequirementsSectionEntity acting = CastingRequirementsSectionEntity.builder()
            .casting(casting)
            .sectionStatus(completedStatus)
            .build();
        castingActingRepository.save(acting);

        CastingRemunerationEntity remuneration = CastingRemunerationEntity.builder()
            .casting(casting)
            .sectionStatus(notStartedStatus)
            .compensationType(compensationPaid)
            .build();
        castingRemunerationsSectionRepository.save(remuneration);

        return String.valueOf(casting.getDefaultCode());
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

        var result = castingRepository.findAll(spec, pageable);
        var castings = result.getContent();

        var ids = castings.stream().map(CastingEntity::getId).toList();

        var gateById = ids.isEmpty()
            ? java.util.Map.<UUID, CastingCardStatusGateProjection>of()
            : castingRepository.findCardsGateForEmployer(employerProfileId, ids)
            .stream()
            .collect(Collectors.toMap(CastingCardStatusGateProjection::getId, Function.identity()));

        return castings.stream()
            .map(c -> {
                var gate = gateById.get(c.getId());
                var allowed = castingStatusTransitionPolicy.allowedNextStatuses(gate);
                return castingMapper.toCardResponse(c, allowed);
            })
            .toList();
    }

    @Override
    public EmployerCastingEditorResponse getCastingEditorBySlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("general.slug_required");
        }

        var p = castingRepository.findCastingEditorProjectionBySlug(slug.trim())
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        boolean publishable =
            isSectionCompleted(p.getBasicInfoSectionStatus())
                && isSectionCompleted(p.getRolesSectionStatus())
                && isSectionCompleted(p.getRequirementsSectionStatus())
                && isSectionCompleted(p.getRemunerationSectionStatus());

        return new EmployerCastingEditorResponse(
            p.getId(),
            p.getDefaultCode(),
            mapToSiteMetadataObject(p.getStatus()),
            p.getBasicInfoSectionId(),
            p.getRolesSectionId(),
            p.getRequirementsSectionId(),
            p.getRemunerationSectionId(),
            mapToSiteMetadataObject(p.getBasicInfoSectionStatus()),
            mapToSiteMetadataObject(p.getRolesSectionStatus()),
            mapToSiteMetadataObject(p.getRequirementsSectionStatus()),
            mapToSiteMetadataObject(p.getRemunerationSectionStatus()),
            publishable
        );
    }

    @Override
    public CastingResponse getEmployerCastingDetailsBySlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("general.slug_required");
        }

        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();
        UUID employerProfileId = employer.employerProfile().getId();

        CastingEntity foundCasting = castingRepository
            .findEmployerDetailsByDefaultCodeAndEmployerProfileId(slug.trim(), employerProfileId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        Long totalCastings = castingRepository.countByEmployerProfileIdAndDeletedFalse(employerProfileId);

        LocalDate memberSince = foundCasting.getEmployerProfile() != null
            && foundCasting.getEmployerProfile().getCreatedAt() != null
            ? foundCasting.getEmployerProfile().getCreatedAt().toLocalDate()
            : null;

        CastingEmployerInfoResponse employerInfo = castingMapper.toCastingEmployerInfoResponse(
            foundCasting.getEmployerProfile(),
            totalCastings,
            memberSince
        );

        return castingMapper.toCastingResponse(foundCasting, employerInfo);
    }

    @Override
    public CastingResponse getPublicCastingDetailsBySlugAndRoleId(String slug, UUID roleId) {
        if (slug == null || slug.isBlank()) throw new IllegalArgumentException("general.slug_required");
        if (roleId == null) throw new IllegalArgumentException("general.role_id_required");

        var allowed = List.of(CASTING_STATUS_PUBLISHED, CASTING_STATUS_CLOSED);

        CastingEntity casting = castingRepository
            .findPublicDetailsBySlugAndRoleId(slug.trim(), roleId, allowed)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        UUID employerProfileId = casting.getEmployerProfile() != null
            ? casting.getEmployerProfile().getId()
            : null;

        Long totalCastings = employerProfileId != null
            ? castingRepository.countPublicCastingsByEmployerProfileId(employerProfileId, allowed)
            : null;

        LocalDate memberSince = casting.getEmployerProfile() != null
            && casting.getEmployerProfile().getCreatedAt() != null
            ? casting.getEmployerProfile().getCreatedAt().toLocalDate()
            : null;

        CastingEmployerInfoResponse employerInfo = castingMapper.toCastingEmployerInfoResponse(
            casting.getEmployerProfile(),
            totalCastings,
            memberSince
        );

        var response = castingMapper.toCastingResponse(casting, employerInfo);

        var filtered = response.requirementsSection().requirements().stream()
            .filter(r -> roleId.equals(r.roleId()))
            .toList();

        var newReqSection = new CastingRequirementsSectionResponse(
            response.requirementsSection().id(),
            response.requirementsSection().sectionStatus(),
            filtered
        );

        return new CastingResponse(
            response.id(),
            response.defaultCode(),
            response.castingStatus(),
            response.employerInfo(),
            response.basicInfoSection(),
            response.rolesSection(),
            newReqSection,
            response.remunerationSection()
        );
    }

    @Override
    @Transactional
    public void deleteCasting(UUID castingId) {
        CastingEntity casting = castingRepository.findById(castingId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        castingRepository.deleteById(castingId);
    }

    // Casting Statuses
    @Override
    @Transactional
    public EmployerCastingEditorResponse publishCasting(UUID castingId) {
        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();
        UUID employerProfileId = employer.employerProfile().getId();

        // 1) Gate ultra-liviano: ownership + estados + deadline + status actual
        var gate = castingRepository.findPublishGateForEmployer(castingId, employerProfileId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        // 2) Validaiton
        castingStatusTransitionPolicy.assertCanPublish(gate);

        CastingStatusOptionEntity published = castingStatusOptionRepository
            .findByStringCode(CASTING_STATUS_PUBLISHED)
            .orElseThrow(() -> new IllegalStateException("sitemetadata.casting_status.not_found"));

        // 3) Update condicionado
        int updated = castingRepository.publishIfAllowed(
            castingId,
            employerProfileId,
            published,
            CASTING_STATUS_DRAFT,
            CASTING_STATUS_PAUSED
        );

        if (updated == 0) {
            // alguien lo cambió entre gate y update, o no pertenece al employer
            throw new IllegalStateException("castings.invalid_status_transition");
        }

        CastingEntity casting = castingRepository.findById(castingId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        return getCastingEditorBySlug(casting.getDefaultCode());
    }

    @Override
    @Transactional
    public EmployerCastingEditorResponse setDraftCasting(UUID castingId) {
        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();
        UUID employerProfileId = employer.employerProfile().getId();

        var gate = castingRepository.findPublishGateForEmployer(castingId, employerProfileId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        castingStatusTransitionPolicy.assertCanSetDraft(gate);

        CastingStatusOptionEntity draft = castingStatusOptionRepository
            .findByStringCode(CASTING_STATUS_DRAFT)
            .orElseThrow(() -> new IllegalStateException("sitemetadata.casting_status.not_found"));

        int updated = castingRepository.setStatusIfCurrentIn(
            castingId,
            employerProfileId,
            draft,
            List.of(CASTING_STATUS_PUBLISHED, CASTING_STATUS_PAUSED)
        );

        if (updated == 0) throw new IllegalStateException("castings.invalid_status_transition");

        CastingEntity casting = castingRepository.findById(castingId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        return getCastingEditorBySlug(casting.getDefaultCode());
    }

    @Override
    @Transactional
    public EmployerCastingEditorResponse pauseCasting(UUID castingId) {
        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();
        UUID employerProfileId = employer.employerProfile().getId();

        var gate = castingRepository.findPublishGateForEmployer(castingId, employerProfileId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        castingStatusTransitionPolicy.assertCanPause(gate);

        CastingStatusOptionEntity paused = castingStatusOptionRepository
            .findByStringCode(CASTING_STATUS_PAUSED)
            .orElseThrow(() -> new IllegalStateException("sitemetadata.casting_status.not_found"));

        int updated = castingRepository.setStatusIfCurrentIn(
            castingId,
            employerProfileId,
            paused,
            List.of(CASTING_STATUS_PUBLISHED)
        );

        if (updated == 0) throw new IllegalStateException("castings.invalid_status_transition");

        CastingEntity casting = castingRepository.findById(castingId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        return getCastingEditorBySlug(casting.getDefaultCode());
    }

    @Override
    @Transactional
    public EmployerCastingEditorResponse closeCasting(UUID castingId) {
        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();
        UUID employerProfileId = employer.employerProfile().getId();

        var gate = castingRepository.findPublishGateForEmployer(castingId, employerProfileId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        castingStatusTransitionPolicy.assertCanClose(gate);

        CastingStatusOptionEntity closed = castingStatusOptionRepository
            .findByStringCode(CASTING_STATUS_CLOSED)
            .orElseThrow(() -> new IllegalStateException("sitemetadata.casting_status.not_found"));

        int updated = castingRepository.setStatusIfCurrentIn(
            castingId,
            employerProfileId,
            closed,
            List.of(CASTING_STATUS_DRAFT, CASTING_STATUS_PUBLISHED, CASTING_STATUS_PAUSED)
        );

        if (updated == 0) throw new IllegalStateException("castings.invalid_status_transition");

        CastingEntity casting = castingRepository.findById(castingId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        return getCastingEditorBySlug(casting.getDefaultCode());
    }

    @Override
    @Transactional
    public EmployerCastingEditorResponse archiveCasting(UUID castingId) {
        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();
        UUID employerProfileId = employer.employerProfile().getId();

        var gate = castingRepository.findPublishGateForEmployer(castingId, employerProfileId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        castingStatusTransitionPolicy.assertCanArchive(gate);

        CastingStatusOptionEntity archived = castingStatusOptionRepository
            .findByStringCode(CASTING_STATUS_ARCHIVED)
            .orElseThrow(() -> new IllegalStateException("sitemetadata.casting_status.not_found"));

        int updated = castingRepository.setStatusIfCurrentIn(
            castingId,
            employerProfileId,
            archived,
            List.of(CASTING_STATUS_DRAFT, CASTING_STATUS_PUBLISHED, CASTING_STATUS_PAUSED, CASTING_STATUS_CLOSED)
        );

        if (updated == 0) throw new IllegalStateException("castings.invalid_status_transition");

        CastingEntity casting = castingRepository.findById(castingId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        return getCastingEditorBySlug(casting.getDefaultCode());
    }

    // Helpers
    private boolean isSectionCompleted(CastingSectionStatusOptionEntity status) {
        if (status == null || status.getStringCode() == null) return false;
        return CASTING_SECTION_STATUS_COMPLETED.equals(status.getStringCode());
    }
}
