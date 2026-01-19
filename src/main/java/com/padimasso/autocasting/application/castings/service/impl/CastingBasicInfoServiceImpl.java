package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.dto.request.section.CastingBasicInfoPatchRequest;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingBasicInfoResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingBasicInfoEntity;
import com.padimasso.autocasting.application.castings.repository.CastingBasicInfoRepository;
import com.padimasso.autocasting.application.castings.service.CastingBasicInfoService;
import com.padimasso.autocasting.application.sitemetadata.model.CastingModalityOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingSectionStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProjectTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingModalityOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingSectionStatusOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.ProjectTypeOptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.*;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingBasicInfoServiceImpl implements CastingBasicInfoService {

    private static final String CASTING_MODALITY_ON_SITE = "sitemetadata.casting_modality.on_site";

    private final CastingBasicInfoRepository castingBasicInfoRepository;
    private final ProjectTypeOptionRepository projectTypeOptionRepository;
    private final CastingModalityOptionRepository castingModalityOptionRepository;
    private final CastingSectionStatusOptionRepository castingSectionStatusOptionRepository;
    private final CastingMapper castingMapper;

    @Override
    public CastingBasicInfoResponse getBySectionId(UUID sectionId) {
        CastingBasicInfoEntity foundSection = castingBasicInfoRepository.findById(sectionId)
            .orElseThrow(() -> new IllegalArgumentException("castings.section.not_found"));
        return castingMapper.toBasicInfoResponse(foundSection);
    }

    @Override
    @Transactional
    public CastingBasicInfoResponse patchCastingBasicInfo(CastingBasicInfoPatchRequest request) {
        CastingBasicInfoEntity basicInfo = castingBasicInfoRepository.findById(request.id())
            .orElseThrow(() -> new IllegalArgumentException("castings.not_found"));

        if (request.title() != null) {
            basicInfo.setTitle(request.title().trim());
        }

        if (request.projectTypeId() != null) {
            ProjectTypeOptionEntity projectType = projectTypeOptionRepository.findById(request.projectTypeId())
                .orElseThrow(() -> new IllegalArgumentException("sitemetadata.project_type.not_found"));
            basicInfo.setProjectType(projectType);
        }

        if (request.castingModalityId() != null) {
            CastingModalityOptionEntity castingModality = castingModalityOptionRepository.findById(request.castingModalityId())
                .orElseThrow(() -> new IllegalArgumentException("sitemetadata.casting_modality.not_found"));
            basicInfo.setCastingModality(castingModality);
        }

        if (request.castingModalityText() != null && request.castingModalityText().isPresent()) {
            basicInfo.setCastingModalityText(request.castingModalityText().orElse(null));
        }

        if (request.applicationDeadline() != null) {
            basicInfo.setApplicationDeadline(request.applicationDeadline());
        }

        if (request.hasWardrobeFitting() != null && request.hasWardrobeFitting().isPresent()) {
            basicInfo.setHasWardrobeFitting(request.hasWardrobeFitting().orElse(null));
        }

        if (request.wardrobeFittingText() != null && request.wardrobeFittingText().isPresent()) {
            basicInfo.setWardrobeFittingText(request.wardrobeFittingText().orElse(null));
        }

        if (request.shootingStartDate() != null && request.shootingStartDate().isPresent()) {
            basicInfo.setShootingStartDate(request.shootingStartDate().orElse(null));
        }

        if (request.shootingEndDate() != null && request.shootingEndDate().isPresent()) {
            basicInfo.setShootingEndDate(request.shootingEndDate().orElse(null));
        }

        if (request.description() != null && request.description().isPresent()) {
            basicInfo.setDescription(request.description().orElse(null));
        }

        updateSectionStatus(basicInfo);

        CastingBasicInfoEntity saved = castingBasicInfoRepository.save(basicInfo);
        return castingMapper.toBasicInfoResponse(saved);
    }

    private void updateSectionStatus(CastingBasicInfoEntity basicInfo) {
        String nextStatusCode = computeNextStatusCode(basicInfo);

        CastingSectionStatusOptionEntity status = castingSectionStatusOptionRepository.findByStringCode(nextStatusCode)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.casting_section_status.not_found"));

        basicInfo.setSectionStatus(status);
    }

    private String computeNextStatusCode(CastingBasicInfoEntity basicInfo) {
        if (isCompleted(basicInfo)) return CASTING_SECTION_STATUS_COMPLETED;
        if (hasAnyValue(basicInfo)) return CASTING_SECTION_STATUS_IN_PROGRESS;
        return CASTING_SECTION_STATUS_NOT_STARTED;
    }

    private boolean isCompleted(CastingBasicInfoEntity basicInfo) {
        boolean hasTitle = hasText(basicInfo.getTitle());
        boolean hasProjectType = basicInfo.getProjectType() != null;
        boolean hasCastingModality = basicInfo.getCastingModality() != null;
        boolean hasDeadline = basicInfo.getApplicationDeadline() != null;

        Boolean wardrobe = basicInfo.getHasWardrobeFitting(); // tri-state
        boolean hasWardrobeAnswered = wardrobe != null;

        boolean hasShootingStart = basicInfo.getShootingStartDate() != null;
        boolean hasShootingEnd = basicInfo.getShootingEndDate() != null;

        String modalityCode = hasCastingModality ? basicInfo.getCastingModality().getStringCode() : null;
        boolean modalityNeedsText = CASTING_MODALITY_ON_SITE.equals(modalityCode);
        boolean hasModalityTextIfRequired = !modalityNeedsText || hasText(basicInfo.getCastingModalityText());

        boolean wardrobeNeedsText = Boolean.TRUE.equals(wardrobe);
        boolean hasWardrobeTextIfRequired = !wardrobeNeedsText || hasText(basicInfo.getWardrobeFittingText());

        return hasTitle
            && hasProjectType
            && hasCastingModality
            && hasModalityTextIfRequired
            && hasDeadline
            && hasWardrobeAnswered
            && hasWardrobeTextIfRequired
            && hasShootingStart
            && hasShootingEnd;
    }

    private boolean hasAnyValue(CastingBasicInfoEntity basicInfo) {
        return hasText(basicInfo.getTitle())
            || basicInfo.getProjectType() != null
            || basicInfo.getCastingModality() != null
            || hasText(basicInfo.getCastingModalityText())
            || basicInfo.getApplicationDeadline() != null
            || basicInfo.getHasWardrobeFitting() != null
            || hasText(basicInfo.getWardrobeFittingText())
            || basicInfo.getShootingStartDate() != null
            || basicInfo.getShootingEndDate() != null
            || hasText(basicInfo.getDescription());
    }

    private boolean hasText(String v) {
        return v != null && !v.trim().isEmpty();
    }
}
