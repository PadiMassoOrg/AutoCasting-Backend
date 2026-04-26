package com.padimasso.autocasting.application.legal.security;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.legal.service.LegalService;
import com.padimasso.autocasting.config.AppConstants;
import com.padimasso.autocasting.exception.ApiErrorFactory;
import com.padimasso.autocasting.exception.ApiException;
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
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class LegalAcceptanceEnforcementFilter extends OncePerRequestFilter {

    private final LegalService legalService;
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
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserEntity user)) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isAdmin = user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getCode()));
        if (isAdmin) {
            filterChain.doFilter(request, response);
            return;
        }

        String locale = resolveLocale(request);
        try {
            boolean acceptedCurrent = legalService.hasAcceptedCurrentRequired(user.getId(), locale);
            if (!acceptedCurrent) {
                apiErrorFactory.write(
                    request,
                    response,
                    HttpStatus.PRECONDITION_REQUIRED,
                    "legal.acceptance.required",
                    new Object[]{locale}
                );
                return;
            }
        } catch (ApiException ex) {
            apiErrorFactory.write(request, response, ex.getStatus(), ex.getMessageKey(), ex.getArgs());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldBypass(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (!uri.startsWith(AppConstants.BASE_API_URL)) {
            return true;
        }
        if (uri.startsWith(AppConstants.BASE_API_URL + "/auth/")) {
            return true;
        }
        if (uri.startsWith(AppConstants.ADMIN_API_URL + "/")) {
            return true;
        }
        if (uri.equals(AppConstants.LEGAL_CURRENT_DOCUMENT_API_URL)
            || uri.equals(AppConstants.LEGAL_REQUIREMENTS_API_URL)
            || uri.equals(AppConstants.LEGAL_ACCEPT_DOCUMENT_API_URL)
            || uri.equals(AppConstants.LEGAL_ACCEPT_CURRENT_API_URL)) {
            return true;
        }
        return false;
    }

    private String resolveLocale(HttpServletRequest request) {
        String language = request.getHeader("Accept-Language");
        if (language == null || language.isBlank()) return "es";
        String normalized = language.toLowerCase(Locale.ROOT).trim();
        return normalized.startsWith("es") ? "es" : "es";
    }
}
