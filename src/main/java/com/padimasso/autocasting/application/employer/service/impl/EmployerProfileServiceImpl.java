package com.padimasso.autocasting.application.employer.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.employer.dto.response.EmployerProfileResponse;
import com.padimasso.autocasting.application.employer.mapper.EmployerProfileMapper;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.employer.service.EmployerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class EmployerProfileServiceImpl implements EmployerProfileService {

    private static final String PROFILE_NOT_FOUND = "profile.not_found";
    private final AuthContext authContext;
    private final EmployerProfileRepository employerProfileRepository;
    private final EmployerProfileMapper employerProfileMapper;

    @Override
    public EmployerProfileResponse getMyProfile() {
        UserEntity user = authContext.getCurrentUserOrThrow();

        var foundProfile = employerProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));

        return employerProfileMapper.toProfileResponse(foundProfile, user);
    }
}
