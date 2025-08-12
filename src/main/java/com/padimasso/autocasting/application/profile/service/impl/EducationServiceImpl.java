package com.padimasso.autocasting.application.profile.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.profile.dto.request.EducationRequest;
import com.padimasso.autocasting.application.profile.dto.response.EducationResponse;
import com.padimasso.autocasting.application.profile.mapper.ProfileMapper;
import com.padimasso.autocasting.application.profile.model.EducationEntity;
import com.padimasso.autocasting.application.profile.repository.EducationRepository;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
import com.padimasso.autocasting.application.profile.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class EducationServiceImpl implements EducationService {

    private final AuthContext authContext;
    private final ProfileRepository profileRepository;
    private final EducationRepository educationRepository;
    private final ProfileMapper profileMapper;

    @Override
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

}
