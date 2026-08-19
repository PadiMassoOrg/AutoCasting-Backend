package com.padimasso.autocasting.application.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.padimasso.autocasting.config.AppProperties;
import com.padimasso.autocasting.exception.ApiException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.AUTH_GOOGLE_MOBILE_NOT_CONFIGURED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.AUTH_GOOGLE_TOKEN_INVALID;

@Service
@RequiredArgsConstructor
public class GoogleIdTokenVerifierService {

    private final AppProperties appProperties;

    private GoogleIdTokenVerifier verifier;

    @PostConstruct
    void init() {
        List<String> audience = new ArrayList<>();
        String iosClientId = appProperties.getGoogle().getMobileIosClientId();
        String androidClientId = appProperties.getGoogle().getMobileAndroidClientId();
        if (iosClientId != null && !iosClientId.isBlank()) {
            audience.add(iosClientId);
        }
        if (androidClientId != null && !androidClientId.isBlank()) {
            audience.add(androidClientId);
        }

        if (!audience.isEmpty()) {
            this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(audience)
                .build();
        }
    }

    public GoogleIdToken.Payload verify(String idToken) {
        if (verifier == null) {
            throw ApiException.internal(AUTH_GOOGLE_MOBILE_NOT_CONFIGURED);
        }

        try {
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw ApiException.unauthorized(AUTH_GOOGLE_TOKEN_INVALID);
            }
            return googleIdToken.getPayload();
        } catch (GeneralSecurityException | java.io.IOException | IllegalArgumentException e) {
            throw ApiException.unauthorized(AUTH_GOOGLE_TOKEN_INVALID);
        }
    }
}
