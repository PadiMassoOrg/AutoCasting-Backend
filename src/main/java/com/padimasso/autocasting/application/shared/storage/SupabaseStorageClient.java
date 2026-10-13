package com.padimasso.autocasting.application.shared.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.padimasso.autocasting.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lists and deletes objects of the media bucket through Supabase's Storage REST API with the service key.
 * Supabase has no delete-by-prefix endpoint, so folders are listed first and deleted by explicit keys.
 */
@Component
@RequiredArgsConstructor
public class SupabaseStorageClient {

    private static final int LIST_PAGE_SIZE = 100;
    private static final int DELETE_BATCH_SIZE = 100;

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public boolean isConfigured() {
        var cfg = appProperties.getSupabase();
        return cfg != null && !isBlank(cfg.getUrl()) && !isBlank(cfg.getServiceRoleKey()) && !isBlank(cfg.getMediaBucket());
    }

    public String mediaBucket() {
        return appProperties.getSupabase().getMediaBucket();
    }

    // Every object under the prefix, including the ones in nested subfolders.
    public List<StoredObject> listObjects(String prefix) throws JsonProcessingException {
        return listUnderPrefix(newClient(), apiBase(), mediaBucket(), prefix);
    }

    public void deleteObjects(List<String> keys) {
        if (keys.isEmpty()) return;

        RestClient client = newClient();
        String deleteUrl = apiBase() + "/storage/v1/object/" + mediaBucket();
        for (int from = 0; from < keys.size(); from += DELETE_BATCH_SIZE) {
            List<String> batch = keys.subList(from, Math.min(from + DELETE_BATCH_SIZE, keys.size()));
            client.method(HttpMethod.DELETE)
                .uri(deleteUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("prefixes", batch))
                .retrieve()
                .toBodilessEntity();
        }
    }

    private List<StoredObject> listUnderPrefix(RestClient client, String apiBase, String bucket, String prefix)
        throws JsonProcessingException {
        List<StoredObject> objects = new ArrayList<>();
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
                    objects.addAll(listUnderPrefix(client, apiBase, bucket, prefix + name + "/"));
                } else {
                    objects.add(new StoredObject(prefix + name, parseCreatedAt(item)));
                }
            }

            if (items.size() < LIST_PAGE_SIZE) break;
            offset += LIST_PAGE_SIZE;
        }

        return objects;
    }

    // A list item is a subfolder pseudo-entry when it has neither an id nor metadata (Storage has no real folders).
    static boolean isFolderEntry(JsonNode item) {
        JsonNode id = item.path("id");
        JsonNode metadata = item.path("metadata");
        return (id.isMissingNode() || id.isNull()) && (metadata.isMissingNode() || metadata.isNull());
    }

    static Instant parseCreatedAt(JsonNode item) {
        String createdAt = item.path("created_at").asText(null);
        if (isBlank(createdAt)) return null;
        try {
            return Instant.parse(createdAt);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private RestClient newClient() {
        var cfg = appProperties.getSupabase();
        return RestClient.builder()
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + cfg.getServiceRoleKey())
            .defaultHeader("apikey", cfg.getServiceRoleKey())
            .build();
    }

    private String apiBase() {
        String url = appProperties.getSupabase().getUrl().trim();
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
