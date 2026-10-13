package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.shared.storage.SupabaseStorageClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaStorageServiceImplTest {

    private static final String BUCKET = "profile-media-public";
    private static final String BASE = "https://x.supabase.co/storage/v1/object/public/" + BUCKET + "/";

    @Mock
    private SupabaseStorageClient storageClient;

    private MediaStorageServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new MediaStorageServiceImpl(storageClient);
    }

    private void configured() {
        when(storageClient.isConfigured()).thenReturn(true);
        when(storageClient.mediaBucket()).thenReturn(BUCKET);
    }

    @Test
    void deleteByPublicUrl_deletesTheObjectAndItsThumbnail() {
        configured();

        service.deleteByPublicUrl(BASE + "talent/p-1/media/headshot/1700000000.webp?v=1");

        verify(storageClient).deleteObjects(List.of(
            "talent/p-1/media/headshot/1700000000.webp", "talent/p-1/media/headshot/1700000000.webp.thumb.webp"));
    }

    @Test
    void deleteByPublicUrl_ignoresBlankUrls() {
        service.deleteByPublicUrl(null);
        service.deleteByPublicUrl("  ");

        verify(storageClient, never()).deleteObjects(anyList());
    }

    @Test
    void deleteByPublicUrl_doesNothingWhenStorageIsNotConfigured() {
        when(storageClient.isConfigured()).thenReturn(false);

        service.deleteByPublicUrl(BASE + "talent/p-1/a.webp");

        verify(storageClient, never()).deleteObjects(anyList());
    }

    @Test
    void deleteByPublicUrl_doesNothingForAnotherBucketOrAnExternalUrl() {
        configured();

        service.deleteByPublicUrl("https://x.supabase.co/storage/v1/object/public/other-bucket/talent/p-1/a.webp");
        service.deleteByPublicUrl("https://example.com/a.jpeg");

        verify(storageClient, never()).deleteObjects(anyList());
    }

    @Test
    void deleteByPublicUrl_neverThrowsWhenStorageFails() {
        configured();
        doThrow(new IllegalStateException("boom")).when(storageClient).deleteObjects(anyList());

        service.deleteByPublicUrl(BASE + "talent/p-1/a.webp");
    }
}
