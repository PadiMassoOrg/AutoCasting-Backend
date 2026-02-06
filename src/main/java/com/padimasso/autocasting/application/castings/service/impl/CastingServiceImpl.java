package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.auth.context.EmployerContext;
import com.padimasso.autocasting.application.auth.dto.response.EmployerPrincipal;
import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.dto.response.CastingEmployerInfoResponse;
import com.padimasso.autocasting.application.castings.dto.response.CastingResponse;
import com.padimasso.autocasting.application.castings.dto.response.EmployerCastingResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingCardResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.*;
import com.padimasso.autocasting.application.castings.repository.*;
import com.padimasso.autocasting.application.castings.repository.order.EmployerCastingsOrderBy;
import com.padimasso.autocasting.application.castings.repository.specification.CastingSpecs;
import com.padimasso.autocasting.application.castings.service.CastingService;
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
            incomingFilter.projectTypeIdTokens(),
            incomingFilter.statusIdToken(),
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

        return result.getContent()
            .stream()
            .map(castingMapper::toCardResponse)
            .toList();
    }

    @Override
    public EmployerCastingResponse getDetailsForEmployerBySlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException("general.slug_required");
        }

        var p = castingRepository.findDetailsProjectionBySlug(slug.trim())
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        boolean publishable =
            isSectionCompleted(p.getBasicInfoSectionStatus())
                && isSectionCompleted(p.getRolesSectionStatus())
                && isSectionCompleted(p.getRequirementsSectionStatus())
                && isSectionCompleted(p.getRemunerationSectionStatus());

        return new EmployerCastingResponse(
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
    @Transactional
    public void deleteCasting(UUID castingId) {
        CastingEntity casting = castingRepository.findById(castingId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        castingRepository.deleteById(castingId);
    }

    // Casting Statuses
    @Override
    @Transactional
    public EmployerCastingResponse publishCasting(UUID castingId) {
        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();
        UUID employerProfileId = employer.employerProfile().getId();

        // 1) Gate ultra-liviano: ownership + estados + deadline + status actual
        var gate = castingRepository.findPublishGateForEmployer(castingId, employerProfileId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        // 2) Validar "publishable" (secciones completas)
        boolean publishable =
            isCompletedCode(gate.getBasicInfoStatusCode())
                && isCompletedCode(gate.getRolesStatusCode())
                && isCompletedCode(gate.getRequirementsStatusCode())
                && isCompletedCode(gate.getRemunerationStatusCode());

        if (!publishable) {
            throw new IllegalStateException("castings.not_publishable");
        }

        // 3) Validar deadline (defensa server-side)
        //    Si deadline es null, igual no debería llegar acá (basicInfo no sería COMPLETED),
        //    pero lo defendemos.
        var deadline = gate.getApplicationDeadline();
        if (deadline == null) {
            throw new IllegalStateException("castings.deadline_required");
        }

        var today = java.time.LocalDate.now();
        if (deadline.isBefore(today)) {
            // opcional: podrías también setear CLOSED automáticamente acá en el futuro
            throw new IllegalStateException("castings.deadline_passed");
        }

        // 4) Validar transición por status actual
        String currentStatusCode = gate.getCastingStatusCode();
        boolean canPublishNow =
            CASTING_STATUS_DRAFT.equals(currentStatusCode) || CASTING_STATUS_PAUSED.equals(currentStatusCode);

        if (!canPublishNow) {
            // bloquea PUBLISHED, CLOSED, ARCHIVED
            throw new IllegalStateException("castings.invalid_status_transition");
        }

        // 5) Resolver status PUBLISHED (por sitemetadata)
        CastingStatusOptionEntity published = castingStatusOptionRepository
            .findByStringCode(CASTING_STATUS_PUBLISHED)
            .orElseThrow(() -> new IllegalStateException("sitemetadata.casting_status.not_found"));

        // 6) Update condicionado (evita carreras y evita cargar CastingEntity)
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

        // 7) Responder datos actualizados
        // Ideal: si tu frontend está en /{slug}, puedes devolver EmployerCastingResponse por slug,
        // pero acá no lo tenemos sin otra query. Dos opciones:
        // A) devolver 204 (más eficiente) y el front refetchea.
        // B) devolver response recargando por id/slug.
        //
        // Como tú ya trabajas con slug y dashboard, recomiendo B por DX (una query extra aceptable).
        // Si quieres CERO queries extra, cambia el controller a 204.
        CastingEntity casting = castingRepository.findById(castingId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        return getDetailsForEmployerBySlug(casting.getDefaultCode());
    }

    private boolean isCompletedCode(String code) {
        return CASTING_SECTION_STATUS_COMPLETED.equals(code);
    }

    // Public
    @Override
    public CastingResponse getDetailsBySlug(String slug) {
        CastingEntity foundCasting = castingRepository
            .findPublicDetailsByDefaultCode(slug)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        UUID employerProfileId = foundCasting.getEmployerProfile() != null ? foundCasting.getEmployerProfile().getId() : null;

        Long totalCastings = null;
        if (employerProfileId != null) {
            totalCastings = castingRepository.countPublicCastingsByEmployerProfileId(
                employerProfileId,
                List.of(CASTING_STATUS_PUBLISHED, CASTING_STATUS_CLOSED)
            );
        }

        LocalDate memberSince = null;
        if (foundCasting.getEmployerProfile() != null && foundCasting.getEmployerProfile().getCreatedAt() != null) {
            memberSince = foundCasting.getEmployerProfile().getCreatedAt().toLocalDate();
        }

        CastingEmployerInfoResponse employerInfo = castingMapper.toCastingEmployerInfoResponse(
            foundCasting.getEmployerProfile(),
            totalCastings,
            memberSince
        );

        return castingMapper.toCastingResponse(foundCasting, employerInfo);
    }

    // Helpers
    private boolean isSectionCompleted(CastingSectionStatusOptionEntity status) {
        if (status == null || status.getStringCode() == null) return false;
        return CASTING_SECTION_STATUS_COMPLETED.equals(status.getStringCode());
    }
}
