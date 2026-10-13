package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.service.CastingMediaCleanupService;
import com.padimasso.autocasting.application.shared.storage.StoredObject;
import com.padimasso.autocasting.application.shared.storage.SupabaseStorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Deletes a closed casting's reference-photo folder from Supabase Storage
 * ({@code employer/{employerProfileId}/castings/{castingId}/}): the folder is listed first because Supabase
 * has no delete-by-prefix endpoint, only delete by explicit keys.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CastingMediaCleanupServiceImpl implements CastingMediaCleanupService {

    private final SupabaseStorageClient storageClient;

    @Override
    public void deleteCastingFolder(UUID employerProfileId, UUID castingId) {
        if (employerProfileId == null || castingId == null) return;

        if (!storageClient.isConfigured()) {
            log.warn("Supabase media storage is not configured; skipping folder cleanup for casting {}", castingId);
            return;
        }

        String prefix = "employer/" + employerProfileId + "/castings/" + castingId + "/";

        try {
            List<String> keys = storageClient.listObjects(prefix).stream().map(StoredObject::key).toList();
            storageClient.deleteObjects(keys);
        } catch (Exception ex) {
            log.warn("Could not clean up Supabase folder for casting {}", castingId, ex);
        }
    }
}
