
package com.padimasso.autocasting.application.auth.context;

import com.padimasso.autocasting.application.auth.dto.response.EmployerPrincipal;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployerContext {

    private static final String PROFILE_NOT_FOUND = "profile.not_found";

    private final AuthContext authContext;
    private final EmployerProfileRepository employerProfileRepository;

    public EmployerPrincipal getCurrentEmployerOrThrow() {
        UserEntity user = authContext.getCurrentUserOrThrow();

        EmployerProfileEntity profile = employerProfileRepository
            .findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));

        return new EmployerPrincipal(user, profile);
    }
}

