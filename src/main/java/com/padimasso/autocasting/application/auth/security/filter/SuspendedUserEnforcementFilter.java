package com.padimasso.autocasting.application.auth.security.filter;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.config.AppConstants;
import com.padimasso.autocasting.exception.ApiErrorFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.AUTH_INVALID_CREDENTIALS;

@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SuspendedUserEnforcementFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final ApiErrorFactory apiErrorFactory;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (HttpMethod.OPTIONS.matches(request.getMethod()) || shouldBypass(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        UserEntity user = resolveCurrentUser(auth);
        if (user == null || !user.isSuspended()) {
            filterChain.doFilter(request, response);
            return;
        }

        apiErrorFactory.write(request, response, HttpStatus.UNAUTHORIZED, AUTH_INVALID_CREDENTIALS, null);
    }

    private boolean shouldBypass(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.equals(AppConstants.ME_API_URL);
    }

    private UserEntity resolveCurrentUser(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof UserEntity user) {
            return user;
        }

        String email = null;
        if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            email = springUser.getUsername();
        } else if (principal instanceof String stringUsername) {
            email = stringUsername;
        }

        if (email == null || email.isBlank()) {
            return null;
        }

        return userRepository.findByEmail(email).orElse(null);
    }
}
