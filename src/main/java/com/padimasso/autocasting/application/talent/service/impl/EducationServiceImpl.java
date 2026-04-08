package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.talent.dto.request.EducationRequest;
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

import java.util.List;
import java.util.UUID;

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
    public void deleteMyEducation(UUID id) {
        EducationEntity education = getOwnEducationOrThrow(id);
        educationRepository.delete(education);
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
