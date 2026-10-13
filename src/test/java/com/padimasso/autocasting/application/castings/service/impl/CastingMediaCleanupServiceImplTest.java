package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.shared.storage.StoredObject;
import com.padimasso.autocasting.application.shared.storage.SupabaseStorageClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CastingMediaCleanupServiceImplTest {

    private static final UUID EMPLOYER = UUID.fromString("00000000-0000-0000-0000-0000000000e1");
    private static final UUID CASTING = UUID.fromString("00000000-0000-0000-0000-0000000000c1");
    private static final String PREFIX = "employer/" + EMPLOYER + "/castings/" + CASTING + "/";

    @Mock
    private SupabaseStorageClient storageClient;

    private CastingMediaCleanupServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CastingMediaCleanupServiceImpl(storageClient);
    }

    @Test
    void deleteCastingFolder_deletesEveryObjectListedUnderTheCastingFolder() throws Exception {
        when(storageClient.isConfigured()).thenReturn(true);
        when(storageClient.listObjects(PREFIX)).thenReturn(List.of(
            new StoredObject(PREFIX + "role-reference-photos/1.webp", null),
            new StoredObject(PREFIX + "role-reference-photos/2.webp", null)));

        service.deleteCastingFolder(EMPLOYER, CASTING);

        verify(storageClient).deleteObjects(List.of(
            PREFIX + "role-reference-photos/1.webp", PREFIX + "role-reference-photos/2.webp"));
    }

    @Test
    void deleteCastingFolder_doesNothingWithoutIds() throws Exception {
        service.deleteCastingFolder(null, CASTING);
        service.deleteCastingFolder(EMPLOYER, null);

        verify(storageClient, never()).listObjects(anyString());
        verify(storageClient, never()).deleteObjects(anyList());
    }

    @Test
    void deleteCastingFolder_doesNothingWhenStorageIsNotConfigured() throws Exception {
        when(storageClient.isConfigured()).thenReturn(false);

        service.deleteCastingFolder(EMPLOYER, CASTING);

        verify(storageClient, never()).listObjects(anyString());
        verify(storageClient, never()).deleteObjects(anyList());
    }

    @Test
    void deleteCastingFolder_neverThrowsWhenStorageFails() throws Exception {
        when(storageClient.isConfigured()).thenReturn(true);
        when(storageClient.listObjects(PREFIX)).thenThrow(new IllegalStateException("boom"));

        service.deleteCastingFolder(EMPLOYER, CASTING);

        verify(storageClient, never()).deleteObjects(anyList());
    }

    @Test
    void deleteCastingFolder_neverThrowsWhenTheDeleteFails() throws Exception {
        when(storageClient.isConfigured()).thenReturn(true);
        when(storageClient.listObjects(PREFIX)).thenReturn(List.of(new StoredObject(PREFIX + "a.webp", null)));
        doThrow(new IllegalStateException("boom")).when(storageClient).deleteObjects(anyList());

        service.deleteCastingFolder(EMPLOYER, CASTING);
    }
}
