package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.talent.service.MediaStorageService;
import com.padimasso.autocasting.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaStorageServiceImpl implements MediaStorageService {

    private final AppProperties appProperties;

    @Override
    public void deleteByPublicUrl(String publicUrl) {
        if (isBlank(publicUrl)) {
            return;
        }

        var cfg = appProperties.getSupabase();
        if (cfg == null || isBlank(cfg.getUrl()) || isBlank(cfg.getServiceRoleKey()) || isBlank(cfg.getMediaBucket())) {
            log.warn("Supabase media storage is not configured; skipping deletion of {}", publicUrl);
            return;
        }

        String objectKey = extractObjectKey(publicUrl, cfg.getMediaBucket());
        if (objectKey == null) {
            log.warn("Could not resolve Supabase object key from media URL {}", publicUrl);
            return;
        }

        String encodedObjectKey = UriUtils.encodePath(objectKey, StandardCharsets.UTF_8);
        String apiBase = trimTrailingSlash(cfg.getUrl());
        String deleteUrl = apiBase + "/storage/v1/object/" + cfg.getMediaBucket() + "/" + encodedObjectKey;

        try {
            RestClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + cfg.getServiceRoleKey())
                .defaultHeader("apikey", cfg.getServiceRoleKey())
                .build()
                .delete()
                .uri(deleteUrl)
                .retrieve()
                .toBodilessEntity();
        } catch (Exception ex) {
            log.warn("Could not delete Supabase media object for key {}", objectKey, ex);
        }
    }

    private static String extractObjectKey(String publicUrl, String bucket) {
        String clean = publicUrl.split("#")[0].split("\\?")[0];
        String marker = "/storage/v1/object/public/" + bucket + "/";
        int idx = clean.indexOf(marker);
        if (idx == -1) {
            return null;
        }

        String encodedKey = clean.substring(idx + marker.length());
        return URLDecoder.decode(encodedKey, StandardCharsets.UTF_8);
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
}
