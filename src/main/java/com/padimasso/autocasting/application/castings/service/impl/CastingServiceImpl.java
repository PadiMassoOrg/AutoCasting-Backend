package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.auth.context.EmployerContext;
import com.padimasso.autocasting.application.auth.dto.response.EmployerPrincipal;
import com.padimasso.autocasting.application.castings.dto.response.CastingCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.CastingResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.*;
import com.padimasso.autocasting.application.castings.repository.*;
import com.padimasso.autocasting.application.castings.service.CastingService;
import com.padimasso.autocasting.application.sitemetadata.model.CastingCompensationTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingSectionStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingCompensationTypeOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingSectionStatusOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingStatusOptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private final CastingRemunerationRepository castingRemunerationRepository;
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
            .findByStringCode(SECTION_STATUS_NOT_STARTED)
            .orElseThrow(() -> new IllegalStateException("sitemetadata.casting_section_status.not_found"));

        CastingCompensationTypeOptionEntity compensationUnpaid = castingCompensationTypeOptionRepository
            .findByStringCode(COMPENSATION_TYPE_UNPAID)
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
            .sectionStatus(notStartedStatus)
            .build();
        castingRolesSectionRepository.save(rolesSection);

        CastingRequirementsSectionEntity acting = CastingRequirementsSectionEntity.builder()
            .casting(casting)
            .sectionStatus(notStartedStatus)
            .build();
        castingActingRepository.save(acting);

        CastingRemunerationEntity remuneration = CastingRemunerationEntity.builder()
            .casting(casting)
            .sectionStatus(notStartedStatus)
            .compensationType(compensationUnpaid)
            .paySameForAllRoles(true)
            .build();
        castingRemunerationRepository.save(remuneration);

        return String.valueOf(casting.getDefaultCode());
    }

    @Override
    public List<CastingCardResponse> getMyCastings() {
        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();

        var castings = castingRepository.findAllByEmployerProfileId(employer.employerProfile().getId());

        return castings.stream()
            .map(castingMapper::toCardResponse)
            .toList();
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
