package com.padimasso.autocasting.application.employer.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.context.EmployerContext;
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
    private final EmployerContext employerContext;
    private final AuthContext authContext;
    private final EmployerProfileRepository employerProfileRepository;
    private final EmployerProfileMapper employerProfileMapper;

    @Override
    public EmployerProfileResponse getMyProfile() {
        var principal = employerContext.getCurrentEmployerOrThrow();
        return employerProfileMapper.toProfileResponse(
            principal.employerProfile(),
            principal.user()
        );
    }
}
