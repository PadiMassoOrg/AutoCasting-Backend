package com.padimasso.autocasting.application.auth.service;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthContext {

    private final UserRepository userRepository;

    public UserEntity getCurrentUserOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("auth.not_authenticated");
        }

        Object principal = authentication.getPrincipal();
        String username = null;
        if (principal instanceof UserEntity user) {
            return user;
        } else if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            username = springUser.getUsername();
        } else if (principal instanceof String stringUsername) {
            username = stringUsername;
        }

        if (username == null) {
            throw new IllegalStateException("auth.invalid_principal");
        }

        return userRepository.findByEmail(username)
            .orElseThrow(() -> new IllegalArgumentException("auth.user_not_found"));
    }
}
