package com.padimasso.autocasting.application.auth.context;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthContext {

    private final UserRepository userRepository;

    public UserEntity getCurrentUserOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw ApiException.unauthorized("auth.not_authenticated");
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
            throw ApiException.unauthorized("auth.invalid_principal");
        }

        return userRepository.findByEmail(username)
            .orElseThrow(() -> ApiException.notFound("auth.user_not_found"));
    }

    public Optional<UserEntity> getCurrentUserOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) return java.util.Optional.empty();
        if (!authentication.isAuthenticated()) return java.util.Optional.empty();
        if (authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            return java.util.Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserEntity user) {
            return java.util.Optional.of(user);
        }

        String username = null;
        if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            username = springUser.getUsername();
        } else if (principal instanceof String stringUsername) {
            username = stringUsername;
        }

        if (username == null || username.isBlank()) return java.util.Optional.empty();

        return userRepository.findByEmail(username);
    }
}
