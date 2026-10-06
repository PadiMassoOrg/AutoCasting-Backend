package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.common.dto.LastModifiedResponse;
import com.padimasso.autocasting.application.talent.dto.request.EducationRequest;
import com.padimasso.autocasting.application.talent.dto.request.EducationUpsertRequest;
import com.padimasso.autocasting.application.talent.dto.response.EducationResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.model.EducationEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.EducationRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.EducationService;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class EducationServiceImpl implements EducationService {

    private final AuthContext authContext;
    private final TalentProfileRepository talentProfileRepository;
    private final EducationRepository educationRepository;
    private final TalentProfileMapper talentProfileMapper;

    @Override
    @Transactional
    public EducationResponse createEducation(EducationRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        var foundProfile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));

        var newEducation = EducationEntity.builder()
            .institution(TextNormalizer.normalizeNullable(request.institution()))
            .courseName(TextNormalizer.normalizeNullable(request.courseName()))
            .graduationYear(TextNormalizer.normalizeNullable(request.graduationYear()))
            .talentProfile(foundProfile)
            .build();

        return talentProfileMapper.toEducationResponse(educationRepository.save(newEducation));
    }

    @Override
    public List<EducationResponse> listMyEducation() {
        TalentProfileEntity profile = getMyProfileOrThrow();

        return educationRepository.findAllByTalentProfileId(profile.getId())
            .stream()
            .map(talentProfileMapper::toEducationResponse)
            .toList();
    }

    @Override
    public EducationResponse getMyEducation(UUID id) {
        EducationEntity education = getOwnEducationOrThrow(id);
        return talentProfileMapper.toEducationResponse(education);
    }

    @Override
    @Transactional
    public EducationResponse patchMyEducation(UUID id, EducationRequest request) {
        EducationEntity education = getOwnEducationOrThrow(id);

        if (request.institution() != null) education.setInstitution(TextNormalizer.normalizeNullable(request.institution()));
        if (request.courseName() != null) education.setCourseName(TextNormalizer.normalizeNullable(request.courseName()));
        if (request.graduationYear() != null) education.setGraduationYear(TextNormalizer.normalizeNullable(request.graduationYear()));

        return talentProfileMapper.toEducationResponse(educationRepository.save(education));
    }

    @Override
    @Transactional
    public LastModifiedResponse deleteMyEducation(UUID id) {
        EducationEntity education = getOwnEducationOrThrow(id);
        UUID profileId = education.getTalentProfile().getId();

        educationRepository.softDelete(education);
        talentProfileRepository.touchModifiedAt(profileId);

        return new LastModifiedResponse(talentProfileRepository.findModifiedAtById(profileId));
    }

    @Override
    @Transactional
    public List<EducationResponse> replaceEducation(TalentProfileEntity profile, List<EducationUpsertRequest> items) {
        Map<UUID, EducationEntity> existingById = educationRepository.findAllByTalentProfileId(profile.getId())
            .stream()
            .collect(Collectors.toMap(EducationEntity::getId, Function.identity()));
        Set<UUID> keptIds = new HashSet<>();
        List<EducationEntity> result = new ArrayList<>();

        for (EducationUpsertRequest item : items) {
            EducationRequest data = item.education();
            EducationEntity education;
            if (item.id() == null) {
                education = EducationEntity.builder().talentProfile(profile).build();
            } else {
                education = existingById.get(item.id());
                if (education == null) {
                    throw new IllegalArgumentException("education.not_found");
                }
                keptIds.add(education.getId());
            }
            education.setInstitution(TextNormalizer.normalizeNullable(data.institution()));
            education.setCourseName(TextNormalizer.normalizeNullable(data.courseName()));
            education.setGraduationYear(TextNormalizer.normalizeNullable(data.graduationYear()));
            result.add(educationRepository.save(education));
        }

        existingById.values().stream()
            .filter(education -> !keptIds.contains(education.getId()))
            .forEach(educationRepository::softDelete);
        talentProfileRepository.touchModifiedAt(profile.getId());

        return result.stream().map(talentProfileMapper::toEducationResponse).toList();
    }

    /* ---------------- Helpers ---------------- */

    private TalentProfileEntity getMyProfileOrThrow() {
        UserEntity user = authContext.getCurrentUserOrThrow();
        return talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));
    }

    private EducationEntity getOwnEducationOrThrow(UUID educationId) {
        TalentProfileEntity myProfile = getMyProfileOrThrow();
        EducationEntity education = educationRepository.findById(educationId)
            .orElseThrow(() -> new IllegalArgumentException("education.not_found"));

        if (!education.getTalentProfile().getId().equals(myProfile.getId())) {
            throw new IllegalArgumentException("education.forbidden");
        }

        return education;
    }

}
