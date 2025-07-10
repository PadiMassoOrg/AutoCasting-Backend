package com.padimasso.autocasting.auth.service;

import com.padimasso.autocasting.auth.model.UserEntity;
import com.padimasso.autocasting.auth.repository.UserRepository;
import com.padimasso.autocasting.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AppProperties appProperties;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "oauth.google.user_missing_email");
            return;
        }

        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "auth.user_not_found");
            return;
        }

        UserEntity user = optionalUser.get();
        String jwt = jwtService.generateToken(user);

        // Redirigir con el token como query param
        String redirectUrl = appProperties.getFrontendUrl() + "?token=" + jwt;
        response.sendRedirect(redirectUrl);
    }
}