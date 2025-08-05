package com.padimasso.autocasting.application.common.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
@SuppressWarnings("unused")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return Optional.of("SYSTEM");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserEntity user) {
            return Optional.of(user.getEmail());
        }

        return Optional.of("SYSTEM");
    }
}
