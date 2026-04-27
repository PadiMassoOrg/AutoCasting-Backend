package com.padimasso.autocasting.application.legal.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.padimasso.autocasting.application.legal.model.LegalDocument;
import com.padimasso.autocasting.application.legal.service.LegalPdfUrlService;
import com.padimasso.autocasting.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LegalPdfUrlServiceImpl implements LegalPdfUrlService {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<String> buildSignedDownloadUrl(LegalDocument document) {
        var cfg = appProperties.getSupabase();
        if (cfg == null || isBlank(cfg.getUrl()) || isBlank(cfg.getServiceRoleKey()) || isBlank(cfg.getLegalBucket())) {
            return Optional.empty();
        }

        String objectKey = buildObjectKey(document);
        String encodedObjectKey = UriUtils.encodePath(objectKey, StandardCharsets.UTF_8);
        String apiBase = trimTrailingSlash(cfg.getUrl());
        String signUrl = apiBase + "/storage/v1/object/sign/" + cfg.getLegalBucket() + "/" + encodedObjectKey;

        try {
            RestClient client = RestClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + cfg.getServiceRoleKey())
                .defaultHeader("apikey", cfg.getServiceRoleKey())
                .build();

            String response = client.post()
                .uri(signUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("expiresIn", cfg.getLegalPdfSignedUrlTtlSeconds()))
                .retrieve()
                .body(String.class);

            if (response == null || response.isBlank()) {
                return Optional.empty();
            }

            JsonNode json = objectMapper.readTree(response);
            String signedPath = pickFirstPresent(json, "signedURL", "signedUrl", "signed_url");
            if (isBlank(signedPath)) {
                return Optional.empty();
            }

            if (signedPath.startsWith("http://") || signedPath.startsWith("https://")) {
                return Optional.of(signedPath);
            }
            // Supabase may return signedURL as "/object/sign/..." (without "/storage/v1").
            String normalizedPath = signedPath;
            if (normalizedPath.startsWith("/object/sign/")) {
                normalizedPath = "/storage/v1" + normalizedPath;
            }
            return Optional.of(apiBase + normalizedPath);
        } catch (Exception ex) {
            log.warn("Could not build signed legal PDF URL for doc {}", document.getId(), ex);
            return Optional.empty();
        }
    }

    @Override
    public String buildObjectKey(LegalDocument document) {
        String prefix = normalizePrefix(appProperties.getSupabase() == null ? null : appProperties.getSupabase().getLegalPrefix());
        String type = document.getType().name().toLowerCase(Locale.ROOT);
        String fileName = type + ".pdf";
        String baseKey = document.getVersion() + "/" + document.getLocale() + "/" + fileName;
        return isBlank(prefix) ? baseKey : prefix + "/" + baseKey;
    }

    private static String pickFirstPresent(JsonNode node, String... fields) {
        for (String field : fields) {
            JsonNode value = node.get(field);
            if (value != null && !value.isNull() && value.isTextual() && !value.asText().isBlank()) {
                return value.asText();
            }
        }
        return null;
    }

    private static String trimTrailingSlash(String value) {
        String v = value == null ? "" : value.trim();
        while (v.endsWith("/")) {
            v = v.substring(0, v.length() - 1);
        }
        return v;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String normalizePrefix(String prefix) {
        if (prefix == null) return "";
        String value = prefix.trim();
        while (value.startsWith("/")) value = value.substring(1);
        while (value.endsWith("/")) value = value.substring(0, value.length() - 1);
        return value;
    }
}
