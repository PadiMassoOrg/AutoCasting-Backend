package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.shared.storage.SupabasePublicUrls;
import com.padimasso.autocasting.application.shared.storage.SupabaseStorageClient;
import com.padimasso.autocasting.application.talent.service.MediaStorageService;
import com.padimasso.autocasting.application.talent.util.MediaThumbnails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaStorageServiceImpl implements MediaStorageService {

    private final SupabaseStorageClient storageClient;

    @Override
    public void deleteByPublicUrl(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) {
            return;
        }

        if (!storageClient.isConfigured()) {
            log.warn("Supabase media storage is not configured; skipping deletion of {}", publicUrl);
            return;
        }

        String objectKey = SupabasePublicUrls.objectKey(publicUrl, storageClient.mediaBucket());
        if (objectKey == null) {
            log.warn("Could not resolve Supabase object key from media URL {}", publicUrl);
            return;
        }

        // Bulk delete ignores keys that don't exist, so photos without a thumbnail (uploaded before
        // thumbnails existed, or non-talent media) are not an error.
        try {
            storageClient.deleteObjects(MediaThumbnails.withThumbnail(objectKey));
        } catch (Exception ex) {
            log.warn("Could not delete Supabase media object for key {}", objectKey, ex);
        }
    }
}
