package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.auth.context.EmployerContext;
import com.padimasso.autocasting.application.auth.dto.response.EmployerPrincipal;
import com.padimasso.autocasting.application.castings.dto.EmployerCastingsFilter;
import com.padimasso.autocasting.application.castings.dto.response.CastingResponse;
import com.padimasso.autocasting.application.castings.dto.response.EmployerCastingResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingCardResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.*;
import com.padimasso.autocasting.application.castings.repository.*;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper.mapToSiteMetadataObject;
import static com.padimasso.autocasting.config.AppConstants.*;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingServiceImpl implements CastingService {

    private static final String CASTING_NOT_FOUND = "castings.not_found";

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

    @Transactional
    @Override
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

        var effectiveFilter = new EmployerCastingsFilter(
            employerProfileId
            // TODO: Filtering coming from UI
        );

        var spec = CastingSpecs.fromFilter(effectiveFilter);

        var pageable = PageRequest.of(
            page,
            Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
            Sort.by(Sort.Direction.DESC, "modifiedAt", "id")
        );

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
            .orElseThrow(() -> new IllegalArgumentException("castings.not_found"));

        return new EmployerCastingResponse(
            p.getId(),
            p.getDefaultCode(),
            mapToSiteMetadataObject(p.getStatus()),
            p.getBasicInfoSectionId(),
            p.getRolesSectionId(),
            p.getRequirementsSectionId(),
            p.getRemunerationSectionId()
        );
    }

    // Public
    @Override
    public CastingResponse getDetailsBySlug(String slug) {
        CastingEntity foundCasting = castingRepository
            .findByDefaultCode(slug)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_NOT_FOUND));

        return castingMapper.toCastingResponse(foundCasting);
    }

}
