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

import java.io.IOException;

@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AppProperties appProperties;
    private final JwtService jwtService;
    private final UserRepository userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth) throws IOException {

        OAuth2User principal = (OAuth2User) auth.getPrincipal();
        String email = principal.getAttribute("email");

        UserEntity user = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("oauth.google.user_missing_email"));

        String jwt = jwtService.generateToken(user);

        // Redirigir con el token como query param
        String redirectUrl = appProperties.getOauthSuccessUrl() + "?token=" + jwt;
        res.sendRedirect(redirectUrl);
    }
}
