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

    @Transactional
    @Override
    public CastingBasicInfoResponse patchCastingBasicInfo(CastingBasicInfoPatchRequest request) {
        CastingBasicInfoEntity basicInfo = castingBasicInfoRepository.findById(request.id())
            .orElseThrow(() -> new IllegalArgumentException("castings.not_found"));

        if (request.title() != null) {
            basicInfo.setTitle(request.title().trim());
        }
        if (request.projectTypeId() != null) {
            ProjectTypeOptionEntity projectType = projectTypeOptionRepository.findById(request.projectTypeId())
                .orElseThrow(() -> new IllegalArgumentException("sitemetadata.color.not_found"));
            basicInfo.setProjectType(projectType);
        }
        if (request.location() != null) {
            basicInfo.setLocationText(request.location().trim());
        }
        if (request.castingModalityId() != null) {
            CastingModalityOptionEntity castingModality = castingModalityOptionRepository.findById(request.castingModalityId())
                .orElseThrow(() -> new IllegalArgumentException("sitemetadata.color.not_found"));
            basicInfo.setCastingModality(castingModality);
        }
        if (request.applicationDeadline() != null) {
            basicInfo.setApplicationDeadline(request.applicationDeadline());
        }
        if (request.hasWardrobeFitting() != null) {
            basicInfo.setHasWardrobeFitting(request.hasWardrobeFitting());
        }
        if (request.wardrobeFittingText() != null) {
            basicInfo.setWardrobeFittingText(request.wardrobeFittingText());
        }
        if (request.shootingStartDate() != null) {
            basicInfo.setShootingStartDate(request.shootingStartDate());
        }
        if (request.shootingEndDate() != null) {
            basicInfo.setShootingEndDate(request.shootingEndDate());
        }
        if (request.description().isPresent()) basicInfo.setDescription(request.description().orElse(null));

        return castingMapper.toBasicInfoResponse(castingBasicInfoRepository.save(basicInfo));
    }
}
