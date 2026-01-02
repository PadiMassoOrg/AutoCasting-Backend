package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.dto.request.CastingBasicInfoPatchRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingBasicInfoResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingBasicInfoEntity;
import com.padimasso.autocasting.application.castings.repository.CastingBasicInfoRepository;
import com.padimasso.autocasting.application.castings.service.CastingBasicInfoService;
import com.padimasso.autocasting.application.sitemetadata.model.CastingModalityOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProjectTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.CastingModalityOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.ProjectTypeOptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingBasicInfoServiceImpl implements CastingBasicInfoService {

    private final CastingBasicInfoRepository castingBasicInfoRepository;
    private final ProjectTypeOptionRepository projectTypeOptionRepository;
    private final CastingModalityOptionRepository castingModalityOptionRepository;
    private final CastingMapper castingMapper;

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
        if (request.castingModalityText().isPresent())
            basicInfo.setCastingModalityText(request.castingModalityText().orElse(null));
        if (request.applicationDeadline() != null) {
            basicInfo.setApplicationDeadline(request.applicationDeadline());
        }
        if (request.hasWardrobeFitting() != null) {
            basicInfo.setHasWardrobeFitting(request.hasWardrobeFitting());
        }
        if (request.wardrobeFittingText().isPresent())
            basicInfo.setWardrobeFittingText(request.wardrobeFittingText().orElse(null));
        if (request.shootingStartDate().isPresent())
            basicInfo.setShootingStartDate(request.shootingStartDate().orElse(null));
        if (request.shootingEndDate().isPresent()) basicInfo.setShootingEndDate(request.shootingEndDate().orElse(null));
        if (request.description().isPresent()) basicInfo.setDescription(request.description().orElse(null));

        return castingMapper.toBasicInfoResponse(castingBasicInfoRepository.save(basicInfo));
    }
}
