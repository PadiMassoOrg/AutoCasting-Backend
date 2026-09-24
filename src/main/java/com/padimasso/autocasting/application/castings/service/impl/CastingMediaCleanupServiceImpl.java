package com.padimasso.autocasting.application.castings.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.padimasso.autocasting.application.castings.service.CastingMediaCleanupService;
import com.padimasso.autocasting.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Deletes a closed casting's reference-photo folder from Supabase Storage
 * ({@code employer/{employerProfileId}/castings/{castingId}/}), via a list-then-bulk-delete
 * pass against Supabase's Storage REST API — Supabase has no delete-by-prefix endpoint, only
 * bulk-delete-by-explicit-keys, so the folder's contents must be listed first.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CastingMediaCleanupServiceImpl implements CastingMediaCleanupService {

    private static final int LIST_PAGE_SIZE = 100;

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    @Override
    public void deleteCastingFolder(UUID employerProfileId, UUID castingId) {
        if (employerProfileId == null || castingId == null) return;

        var cfg = appProperties.getSupabase();
        if (cfg == null || isBlank(cfg.getUrl()) || isBlank(cfg.getServiceRoleKey()) || isBlank(cfg.getMediaBucket())) {
            log.warn("Supabase media storage is not configured; skipping folder cleanup for casting {}", castingId);
            return;
        }

        String prefix = "employer/" + employerProfileId + "/castings/" + castingId + "/";
        String apiBase = trimTrailingSlash(cfg.getUrl());

        try {
            RestClient client = RestClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + cfg.getServiceRoleKey())
                .defaultHeader("apikey", cfg.getServiceRoleKey())
                .build();

            List<String> keys = listObjectKeysUnderPrefix(client, apiBase, cfg.getMediaBucket(), prefix);
            if (keys.isEmpty()) return;

            deleteObjectKeys(client, apiBase, cfg.getMediaBucket(), keys);
        } catch (Exception ex) {
            log.warn("Could not clean up Supabase folder for casting {}", castingId, ex);
        }
    }

    /**
     * Supabase Storage's list endpoint only returns the immediate children of a prefix — a
     * nested subfolder comes back as a single pseudo-entry (identifiable by a null/absent
     * {@code metadata}, since Storage has no real directory objects) rather than being expanded
     * automatically. Our layout nests role photos one level deeper
     * ({@code .../castings/{castingId}/role-reference-photos/{file}}), so listing must recurse
     * into any such subfolder entry to reach the actual files.
     */
    private List<String> listObjectKeysUnderPrefix(RestClient client, String apiBase, String bucket, String prefix)
        throws JsonProcessingException {
        List<String> keys = new ArrayList<>();
        String listUrl = apiBase + "/storage/v1/object/list/" + bucket;
        int offset = 0;

        while (true) {
            String response = client.post()
                .uri(listUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("prefix", prefix, "limit", LIST_PAGE_SIZE, "offset", offset))
                .retrieve()
                .body(String.class);

            if (response == null || response.isBlank()) break;

            JsonNode items = objectMapper.readTree(response);
            if (!items.isArray() || items.isEmpty()) break;

            for (JsonNode item : items) {
                String name = item.path("name").asText(null);
                if (isBlank(name)) continue;

                if (isFolderEntry(item)) {
                    keys.addAll(listObjectKeysUnderPrefix(client, apiBase, bucket, prefix + name + "/"));
                } else {
                    keys.add(prefix + name);
                }
            }

            if (items.size() < LIST_PAGE_SIZE) break;
            offset += LIST_PAGE_SIZE;
        }

        return keys;
    }

    private void deleteObjectKeys(RestClient client, String apiBase, String bucket, List<String> keys) {
        String deleteUrl = apiBase + "/storage/v1/object/" + bucket;

        client.method(HttpMethod.DELETE)
            .uri(deleteUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Map.of("prefixes", keys))
            .retrieve()
            .toBodilessEntity();
    }

    /**
     * Package-visible so it can be unit-tested directly, without mocking the RestClient/HTTP
     * chain (this codebase is unit-only, no Testcontainers) — see CastingMediaCleanupServiceImplTest.
     * A Supabase Storage list item is a subfolder pseudo-entry (not a real file) when it has
     * neither an {@code id} nor {@code metadata}, since Storage has no real directory objects.
     * Uses isMissingNode() || isNull() rather than isNull() alone: Jackson's .path() returns a
     * MissingNode (isNull() == false) for an absent field, distinct from an explicit JSON null.
     */
    static boolean isFolderEntry(JsonNode item) {
        JsonNode id = item.path("id");
        JsonNode metadata = item.path("metadata");
        return (id.isMissingNode() || id.isNull()) && (metadata.isMissingNode() || metadata.isNull());
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
