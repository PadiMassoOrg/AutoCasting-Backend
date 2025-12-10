package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.auth.context.EmployerContext;
import com.padimasso.autocasting.application.auth.dto.response.EmployerPrincipal;
import com.padimasso.autocasting.application.castings.model.*;
import com.padimasso.autocasting.application.castings.repository.*;
import com.padimasso.autocasting.application.castings.service.CastingService;
import com.padimasso.autocasting.application.sitemetadata.model.CastingActingModeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingCompensationTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingSectionStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingActingModeOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingCompensationTypeOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingSectionStatusOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingStatusOptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingServiceImpl implements CastingService {

    private static final String CASTING_STATUS_DRAFT = "sitemetadata.casting_status.draft";
    private static final String SECTION_STATUS_NOT_STARTED = "sitemetadata.casting_section_status.not_started";
    private static final String ACTING_MODE_NONE = "sitemetadata.acting_mode.none";
    private static final String COMPENSATION_TYPE_UNPAID = "sitemetadata.compensation_type.unpaid";

    private final EmployerContext employerContext;
    private final CastingRepository castingRepository;
    private final CastingBasicInfoRepository castingBasicInfoRepository;
    private final CastingRolesSectionRepository castingRolesSectionRepository;
    private final CastingActingRepository castingActingRepository;
    private final CastingRemunerationRepository castingRemunerationRepository;
    private final CastingStatusOptionRepository castingStatusOptionRepository;
    private final CastingSectionStatusOptionRepository castingSectionStatusOptionRepository;
    private final CastingActingModeOptionRepository castingActingModeOptionRepository;
    private final CastingCompensationTypeOptionRepository castingCompensationTypeOptionRepository;

    @Transactional
    @Override
    public String createEmptyCasting() {
        EmployerPrincipal employer = employerContext.getCurrentEmployerOrThrow();

        CastingStatusOptionEntity draftStatus = castingStatusOptionRepository
            .findByStringCode(CASTING_STATUS_DRAFT)
            .orElseThrow(() -> new IllegalStateException("casting_status.draft_not_seeded"));

        CastingSectionStatusOptionEntity notStartedStatus = castingSectionStatusOptionRepository
            .findByStringCode(SECTION_STATUS_NOT_STARTED)
            .orElseThrow(() -> new IllegalStateException("casting_section_status.not_started_not_seeded"));

        CastingActingModeOptionEntity actingModeNone = castingActingModeOptionRepository
            .findByStringCode(ACTING_MODE_NONE)
            .orElseThrow(() -> new IllegalStateException("casting_acting_mode.none_not_seeded"));

        CastingCompensationTypeOptionEntity compensationUnpaid = castingCompensationTypeOptionRepository
            .findByStringCode(COMPENSATION_TYPE_UNPAID)
            .orElseThrow(() -> new IllegalStateException("compensation_type.unpaid_not_seeded"));

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

        CastingActingEntity acting = CastingActingEntity.builder()
            .casting(casting)
            .sectionStatus(notStartedStatus)
            .actingMode(actingModeNone)
            .build();
        castingActingRepository.save(acting);

        CastingRemunerationEntity remuneration = CastingRemunerationEntity.builder()
            .casting(casting)
            .sectionStatus(notStartedStatus)
            .compensationType(compensationUnpaid)
            .paySameForAllRoles(true)
            .build();
        castingRemunerationRepository.save(remuneration);

        return String.valueOf(casting.getId());
    }
}
