package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRemunerationPatchRequest;
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
import com.padimasso.autocasting.application.castings.service.internal.CastingRemunerationSectionStatusService;
import com.padimasso.autocasting.application.castings.service.internal.CastingStatusService;
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
import java.util.Objects;
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
    private final CastingRemunerationSectionStatusService remunerationSectionStatusService;
    private final CastingStatusService castingStatusService;
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

        String prevCode = section.getCompensationType() != null ? section.getCompensationType().getStringCode() : null;

        CastingCompensationTypeOptionEntity nextType = castingCompensationTypeOptionRepository
            .findById(request.castingCompensationTypeId())
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.casting_compensation_type.not_found"));

        String nextCode = nextType.getStringCode();

        boolean typeChanged = !Objects.equals(prevCode, nextCode);

        section.setCompensationType(nextType);

        // Si cambió el tipo, y el cliente no mandó notes explícitamente: limpiarlas
        if (typeChanged && (request.notes() == null || !request.notes().isPresent())) {
            section.setNotes(null);
        } else if (request.notes() != null && request.notes().isPresent()) {
            section.setNotes(request.notes().orElse(null));
        }

        CastingRemunerationEntity saved = castingRemunerationsSectionRepository.save(section);

        if (typeChanged) {
            resetRoleRemunerationsOnCompTypeChange(saved.getId());
        }

        if (CASTING_COMPENSATION_TYPE_COLLABORATIVE.equals(nextCode)) {
            applyCollaborativeDefaults(saved.getId());
        }

        updateSectionStatus(saved.getId());

        UUID castingId = saved.getCasting() != null ? saved.getCasting().getId() : null;
        if (castingId != null) {
            remunerationSectionStatusService.recomputeForCasting(castingId);
            castingStatusService.recomputeAfterSectionChange(castingId);
        }

        return getBySectionId(saved.getId());
    }

    @Override
    @Transactional
    public CastingRoleRemunerationResponse patchRoleRemuneration(CastingRoleRemunerationPatchRequest request) {
        CastingRoleRemunerationEntity remuneration = castingRoleRemunerationRepository.findById(request.id())
            .orElseThrow(() -> new IllegalArgumentException("casting.role.remuneration.not_found"));

        CastingRemunerationEntity section = resolveSection(remuneration);
        boolean isCollaborative = section != null
            && section.getCompensationType() != null
            && CASTING_COMPENSATION_TYPE_COLLABORATIVE.equals(section.getCompensationType().getStringCode());

        if (!isCollaborative) {
            if (request.payRateTypeId() != null) {
                PayRateTypeOptionEntity found = payRateTypeOptionRepository.findById(request.payRateTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("sitemetadata.pay_rate_type.not_found"));
                remuneration.setPayRateType(found);
            }

            if (request.currencyId() != null) {
                CurrencyOptionEntity found = currencyOptionRepository.findById(request.currencyId())
                    .orElseThrow(() -> new IllegalArgumentException("sitemetadata.currency.not_found"));
                remuneration.setCurrency(found);
            }

            if (request.amount() != null && request.amount().isPresent()) {
                remuneration.setAmount(request.amount().orElse(null));
            }
        }

        if (request.notes() != null && request.notes().isPresent()) {
            remuneration.setNotes(request.notes().orElse(null));
        }

        if (isCollaborative) {
            ensureDefaults(remuneration);
            forceUnpaid(remuneration);
        } else {
            recomputeIsComplete(remuneration);
        }

        CastingRoleRemunerationEntity saved = castingRoleRemunerationRepository.save(remuneration);

        UUID sectionId = section != null ? section.getId() : null;
        if (sectionId != null) {
            updateSectionStatus(sectionId);
        }

        UUID castingId = null;
        if (saved.getCastingRole() != null
            && saved.getCastingRole().getRolesSection() != null
            && saved.getCastingRole().getRolesSection().getCasting() != null) {
            castingId = saved.getCastingRole().getRolesSection().getCasting().getId();
        }
        if (castingId != null) {
            remunerationSectionStatusService.recomputeForCasting(castingId);
            castingStatusService.recomputeAfterSectionChange(castingId);
        }

        return castingMapper.toRoleRemunerationResponse(saved);
    }

    // Helpers
    private void applyCollaborativeDefaults(UUID sectionId) {
        List<CastingRoleRemunerationEntity> entities =
            castingRoleRemunerationRepository.findAllByRemunerationSectionId(sectionId);

        List<CastingRoleRemunerationEntity> active = entities == null
            ? List.of()
            : entities.stream().filter(r -> r != null && !r.isDeleted()).toList();

        if (active.isEmpty()) return;

        PayRateTypeOptionEntity unpaid = payRateTypeOptionRepository.findByStringCode(PAY_RATE_TYPE_UNPAID)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.pay_rate_type.not_found"));

        CurrencyOptionEntity ars = currencyOptionRepository.findByStringCode(CURRENCY_ARS)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.currency.not_found"));

        List<CastingRoleRemunerationEntity> next = active.stream().map(r -> {
            r.setPayRateType(unpaid);
            r.setCurrency(ars);
            r.setAmount(null);
            r.setNotes(null);
            r.setComplete(true);
            return r;
        }).toList();

        castingRoleRemunerationRepository.saveAll(next);
    }

    private CastingRemunerationEntity resolveSection(CastingRoleRemunerationEntity rr) {
        if (rr == null) return null;
        if (rr.getCastingRole() == null) return null;
        if (rr.getCastingRole().getRolesSection() == null) return null;
        if (rr.getCastingRole().getRolesSection().getCasting() == null) return null;
        return rr.getCastingRole().getRolesSection().getCasting().getRemuneration();
    }

    private void ensureDefaults(CastingRoleRemunerationEntity remuneration) {
        if (remuneration.getPayRateType() == null) {
            PayRateTypeOptionEntity unpaid = payRateTypeOptionRepository.findByStringCode(PAY_RATE_TYPE_UNPAID)
                .orElseThrow(() -> new IllegalArgumentException("sitemetadata.pay_rate_type.not_found"));
            remuneration.setPayRateType(unpaid);
        }

        if (remuneration.getCurrency() == null) {
            CurrencyOptionEntity ars = currencyOptionRepository.findByStringCode(CURRENCY_ARS)
                .orElseThrow(() -> new IllegalArgumentException("sitemetadata.currency.not_found"));
            remuneration.setCurrency(ars);
        }
    }

    private void forceUnpaid(CastingRoleRemunerationEntity remuneration) {
        PayRateTypeOptionEntity unpaid = payRateTypeOptionRepository.findByStringCode(PAY_RATE_TYPE_UNPAID)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.pay_rate_type.not_found"));
        remuneration.setPayRateType(unpaid);
        remuneration.setAmount(null);
        remuneration.setComplete(true);
    }

    private void recomputeIsComplete(CastingRoleRemunerationEntity remuneration) {
        boolean isUnpaid = remuneration.getPayRateType() != null
            && PAY_RATE_TYPE_UNPAID.equals(remuneration.getPayRateType().getStringCode());

        if (isUnpaid) {
            remuneration.setAmount(null);
            remuneration.setComplete(true);
            return;
        }

        boolean complete = remuneration.getPayRateType() != null
            && remuneration.getCurrency() != null
            && remuneration.getAmount() != null;

        remuneration.setComplete(complete);
    }

    private void updateSectionStatus(UUID sectionId) {
        CastingRemunerationEntity section = castingRemunerationsSectionRepository.findById(sectionId)
            .orElseThrow(() -> new IllegalArgumentException("castings.section.not_found"));

        List<CastingRoleRemunerationEntity> entities =
            castingRoleRemunerationRepository.findAllByRemunerationSectionId(sectionId);

        List<CastingRoleRemunerationEntity> active = entities == null
            ? List.of()
            : entities.stream().filter(r -> r != null && !r.isDeleted()).toList();

        String nextStatusCode;
        if (active.isEmpty()) {
            nextStatusCode = CASTING_SECTION_STATUS_NOT_STARTED;
        } else {
            boolean allComplete = active.stream().allMatch(CastingRoleRemunerationEntity::isComplete);
            nextStatusCode = allComplete ? CASTING_SECTION_STATUS_COMPLETED : CASTING_SECTION_STATUS_IN_PROGRESS;
        }

        CastingSectionStatusOptionEntity status = castingSectionStatusOptionRepository.findByStringCode(nextStatusCode)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.casting_section_status.not_found"));

        section.setSectionStatus(status);
        castingRemunerationsSectionRepository.save(section);
    }

    private void resetRoleRemunerationsOnCompTypeChange(UUID sectionId) {
        List<CastingRoleRemunerationEntity> entities =
            castingRoleRemunerationRepository.findAllByRemunerationSectionId(sectionId);

        List<CastingRoleRemunerationEntity> active = entities == null
            ? List.of()
            : entities.stream().filter(r -> r != null && !r.isDeleted()).toList();

        if (active.isEmpty()) return;

        PayRateTypeOptionEntity unpaid = payRateTypeOptionRepository.findByStringCode(PAY_RATE_TYPE_UNPAID)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.pay_rate_type.not_found"));

        CurrencyOptionEntity ars = currencyOptionRepository.findByStringCode(CURRENCY_ARS)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.currency.not_found"));

        List<CastingRoleRemunerationEntity> next = active.stream().map(r -> {
            r.setNotes(null);
            r.setAmount(null);
            r.setPayRateType(unpaid);
            r.setCurrency(ars);
            r.setComplete(false);
            return r;
        }).toList();

        castingRoleRemunerationRepository.saveAll(next);
    }
}
