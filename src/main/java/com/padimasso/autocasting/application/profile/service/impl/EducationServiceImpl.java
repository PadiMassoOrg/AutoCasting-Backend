package com.padimasso.autocasting.application.profile.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.profile.dto.request.EducationRequest;
import com.padimasso.autocasting.application.profile.dto.response.EducationResponse;
import com.padimasso.autocasting.application.profile.mapper.ProfileMapper;
import com.padimasso.autocasting.application.profile.model.EducationEntity;
import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import com.padimasso.autocasting.application.profile.repository.EducationRepository;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
import com.padimasso.autocasting.application.profile.service.EducationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class EducationServiceImpl implements EducationService {

    private final AuthContext authContext;
    private final ProfileRepository profileRepository;
    private final EducationRepository educationRepository;
    private final ProfileMapper profileMapper;

    @Override
    @Transactional
    public EducationResponse createEducation(EducationRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        var foundProfile = profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));

        var newEducation = EducationEntity.builder()
            .institution(request.institution())
            .courseName(request.courseName())
            .graduationYear(request.graduationYear())
            .profile(foundProfile)
            .build();

        return profileMapper.toEducationResponse(educationRepository.save(newEducation));
    }

    @Override
    public List<EducationResponse> listMyEducation() {
        ProfileEntity profile = getMyProfileOrThrow();

        return educationRepository.findAllByProfileId(profile.getId())
            .stream()
            .map(profileMapper::toEducationResponse)
            .toList();
    }

    @Override
    public EducationResponse getMyEducation(UUID id) {
        EducationEntity education = getOwnEducationOrThrow(id);
        return profileMapper.toEducationResponse(education);
    }

    @Override
    @Transactional
    public EducationResponse patchMyEducation(UUID id, EducationRequest request) {
        EducationEntity education = getOwnEducationOrThrow(id);

        if (request.institution() != null) education.setInstitution(request.institution());
        if (request.courseName() != null) education.setCourseName(request.courseName());
        if (request.graduationYear() != null) education.setGraduationYear(request.graduationYear());

        return profileMapper.toEducationResponse(educationRepository.save(education));
    }

    @Override
    @Transactional
    public void deleteMyEducation(UUID id) {
        EducationEntity education = getOwnEducationOrThrow(id);
        educationRepository.delete(education);
    }

    /* ---------------- Helpers ---------------- */

    private ProfileEntity getMyProfileOrThrow() {
        UserEntity user = authContext.getCurrentUserOrThrow();
        return profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
    }

    private EducationEntity getOwnEducationOrThrow(UUID educationId) {
        ProfileEntity myProfile = getMyProfileOrThrow();
        EducationEntity education = educationRepository.findById(educationId)
            .orElseThrow(() -> new IllegalArgumentException("education.not_found"));

        if (!education.getProfile().getId().equals(myProfile.getId())) {
            throw new IllegalArgumentException("education.forbidden");
        }

        return education;
    }

}
