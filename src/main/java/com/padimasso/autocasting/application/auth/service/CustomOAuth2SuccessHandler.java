package com.padimasso.autocasting.application.auth.service;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.legal.service.LegalService;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.config.AppConstants;
import com.padimasso.autocasting.config.AppProperties;
import com.padimasso.autocasting.exception.ApiErrorFactory;
import com.padimasso.autocasting.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Locale;

@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AppProperties appProperties;
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final TalentProfileRepository talentProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final LegalService legalService;
    private final ApiErrorFactory apiErrorFactory;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth) throws IOException {
        OAuth2User principal = (OAuth2User) auth.getPrincipal();
        String email = principal.getAttribute("email");

        UserEntity user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            apiErrorFactory.write(req, res, HttpStatus.UNAUTHORIZED, "oauth.google.user_missing_email", null);
            return;
        }

        try {
            legalService.acceptCurrentRequired(
                user.getId(),
                resolveLocale(req),
                clientIp(req),
                req.getHeader("User-Agent")
            );
        } catch (ApiException ex) {
            apiErrorFactory.write(req, res, ex.getStatus(), ex.getMessageKey(), ex.getArgs());
            return;
        }

        var profileOpt = talentProfileRepository.findByUserId(user.getId());
        String talentProfileSlug = profileOpt.map(TalentProfileEntity::getPublicSlug).orElse(null);

        var employerProfileOpt = employerProfileRepository.findByUserId(user.getId());
        String employerProfileSlug = employerProfileOpt.map(EmployerProfileEntity::getPublicSlug).orElse(null);

        String jwt = jwtService.generateTokenWithCustomExpirationTime(user, AppConstants.EXPIRATION_TIME, talentProfileSlug, employerProfileSlug);

        // Redirigir con el token como query param
        String redirectUrl = appProperties.getOauthSuccessUrl() + "?token=" + jwt;
        res.sendRedirect(redirectUrl);
    }

    private String clientIp(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) return h.split(",")[0].trim();
        return req.getRemoteAddr();
    }

    private String resolveLocale(HttpServletRequest request) {
        String language = request.getHeader("Accept-Language");
        if (language == null || language.isBlank()) return "es";
        String normalized = language.toLowerCase(Locale.ROOT).trim();
        return normalized.startsWith("es") ? "es" : "es";
    }
}
