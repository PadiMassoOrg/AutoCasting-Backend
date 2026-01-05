package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.dto.request.CastingRemunerationPatchRequest;
import com.padimasso.autocasting.application.castings.dto.request.section.CastingRemunerationsSectionPatchRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleRemunerationResponse;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleRemunerationRowResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRemunerationsSectionResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingRemunerationEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleRemunerationEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRemunerationsSectionRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRemunerationRepository;
import com.padimasso.autocasting.application.castings.service.CastingRemunerationService;
import com.padimasso.autocasting.application.sitemetadata.model.CastingCompensationTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingSectionStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CurrencyOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.PayRateTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingCompensationTypeOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingSectionStatusOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.CurrencyOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.PayRateTypeOptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.*;

@Service
@RequiredArgsConstructor
public class CastingRemunerationServiceImpl implements CastingRemunerationService {

    private final CastingRemunerationsSectionRepository castingRemunerationsSectionRepository;
    private final CastingRoleRemunerationRepository castingRoleRemunerationRepository;
    private final CastingSectionStatusOptionRepository castingSectionStatusOptionRepository;
    private final CastingCompensationTypeOptionRepository castingCompensationTypeOptionRepository;
    private final PayRateTypeOptionRepository payRateTypeOptionRepository;
    private final CurrencyOptionRepository currencyOptionRepository;
    private final CastingMapper castingMapper;

    @Override
    public CastingRemunerationsSectionResponse getBySectionId(UUID sectionId) {
        CastingRemunerationEntity foundSection = castingRemunerationsSectionRepository.findById(sectionId)
            .orElseThrow(() -> new IllegalArgumentException("castings.section.not_found"));

        List<CastingRoleRemunerationEntity> entities =
            castingRoleRemunerationRepository.findAllByRemunerationSectionId(sectionId);

        List<CastingRoleRemunerationRowResponse> rows = entities.stream()
            .map(castingMapper::toRoleRemunerationRowResponse)
            .filter(java.util.Objects::nonNull)
            .toList();

        return castingMapper.toRemunerationsSectionResponse(foundSection, rows);
    }

    @Override
    @Transactional
    public CastingRemunerationsSectionResponse patchSectionRemuneration(CastingRemunerationsSectionPatchRequest request) {
        CastingRemunerationEntity section = castingRemunerationsSectionRepository.findById(request.id())
            .orElseThrow(() -> new IllegalArgumentException("castings.section.not_found"));

        CastingCompensationTypeOptionEntity compensationTypeOption = castingCompensationTypeOptionRepository.findById(request.castingCompensationTypeId())
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.casting_compensation_type.not_found"));

        section.setCompensationType(compensationTypeOption);

        List<CastingRoleRemunerationEntity> entities =
            castingRoleRemunerationRepository.findAllByRemunerationSectionId(section.getId());

        updateSectionStatus(section, entities);
        CastingRemunerationEntity savedSection = castingRemunerationsSectionRepository.save(section);

        List<CastingRoleRemunerationRowResponse> rows = entities.stream()
            .map(castingMapper::toRoleRemunerationRowResponse)
            .filter(java.util.Objects::nonNull)
            .toList();

        return castingMapper.toRemunerationsSectionResponse(savedSection, rows);

    }

    @Override
    @Transactional
    public CastingRoleRemunerationResponse patchRoleRemuneration(CastingRemunerationPatchRequest request) {
        CastingRoleRemunerationEntity remuneration = castingRoleRemunerationRepository.findById(request.id())
            .orElseThrow(() -> new IllegalArgumentException("casting.role.remuneration.not_found"));

        if (request.payRateTypeId().isPresent()) {
            UUID payRateTypeId = request.payRateTypeId().get();
            if (payRateTypeId == null) {
                remuneration.setPayRateType(null);
            } else {
                PayRateTypeOptionEntity found = payRateTypeOptionRepository.findById(payRateTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("sitemetadata.pay_rate_type.not_found"));
                remuneration.setPayRateType(found);
            }
        }

        if (request.currencyId().isPresent()) {
            UUID currencyId = request.currencyId().get();
            if (currencyId == null) {
                remuneration.setCurrency(null);
            } else {
                CurrencyOptionEntity found = currencyOptionRepository.findById(currencyId)
                    .orElseThrow(() -> new IllegalArgumentException("sitemetadata.currency.not_found"));
                remuneration.setCurrency(found);
            }
        }

        if (request.amount().isPresent())
            remuneration.setAmount(request.amount().orElse(null));

        if (request.notes().isPresent())
            remuneration.setNotes(request.notes().orElse(null));

        boolean isUnpaid = remuneration.getPayRateType() != null
            && PAY_RATE_TYPE_UNPAID.equals(remuneration.getPayRateType().getStringCode());

        boolean complete = isUnpaid || (
            remuneration.getPayRateType() != null
                && remuneration.getCurrency() != null
                && remuneration.getAmount() != null
        );
        remuneration.setComplete(complete);

        CastingRoleRemunerationEntity saved = castingRoleRemunerationRepository.save(remuneration);

        CastingRemunerationEntity section = saved.getCastingRole()
            .getRolesSection()
            .getCasting()
            .getRemuneration();

        List<CastingRoleRemunerationEntity> entities =
            castingRoleRemunerationRepository.findAllByRemunerationSectionId(section.getId());

        updateSectionStatus(section, entities);
        castingRemunerationsSectionRepository.save(section);

        return castingMapper.toRoleRemunerationResponse(saved);
    }

    private void updateSectionStatus(CastingRemunerationEntity section, List<CastingRoleRemunerationEntity> remunerations) {
        String nextStatusCode;

        if (remunerations == null || remunerations.isEmpty()) {
            nextStatusCode = CASTING_SECTION_STATUS_NOT_STARTED;
        } else {
            boolean allComplete = remunerations.stream().allMatch(CastingRoleRemunerationEntity::isComplete);
            nextStatusCode = allComplete ? CASTING_SECTION_STATUS_COMPLETED : CASTING_SECTION_STATUS_IN_PROGRESS;
        }

        CastingSectionStatusOptionEntity status = castingSectionStatusOptionRepository.findByStringCode(nextStatusCode)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.casting_section_status.not_found"));

        section.setSectionStatus(status);
    }

}


