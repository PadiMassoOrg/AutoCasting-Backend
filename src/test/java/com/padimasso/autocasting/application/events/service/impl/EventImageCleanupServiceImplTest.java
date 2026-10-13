package com.padimasso.autocasting.application.events.service.impl;

import com.padimasso.autocasting.application.common.model.MaintenanceTaskRunEntity;
import com.padimasso.autocasting.application.common.repository.MaintenanceTaskRunRepository;
import com.padimasso.autocasting.application.events.dto.response.EventImageCleanupResponse;
import com.padimasso.autocasting.application.events.dto.response.EventImageCleanupStatusResponse;
import com.padimasso.autocasting.application.events.repository.EventRepository;
import com.padimasso.autocasting.application.shared.storage.StoredObject;
import com.padimasso.autocasting.application.shared.storage.SupabaseStorageClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class EventImageCleanupServiceImplTest {

    private static final String BUCKET = "profile-media-public";
    private static final String BASE = "https://x.supabase.co/storage/v1/object/public/" + BUCKET + "/";
    private static final String USER = "0b4e8f0e-1111-4222-8333-444455556666";
    private static final String LIVE_KEY = "events/" + USER + "/1700000001.webp";
    private static final String DEAD_KEY = "events/" + USER + "/1700000000.webp";
    private static final Instant LONG_AGO = Instant.now().minus(Duration.ofDays(30));

    @Mock
    private EventRepository eventRepository;
    @Mock
    private SupabaseStorageClient storageClient;
    @Mock
    private MaintenanceTaskRunRepository taskRunRepository;

    private EventImageCleanupServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EventImageCleanupServiceImpl(eventRepository, storageClient, taskRunRepository);
    }

    private void storageConfigured() {
        when(storageClient.isConfigured()).thenReturn(true);
        when(storageClient.mediaBucket()).thenReturn(BUCKET);
    }

    private void liveEventAndOneDeadImage() throws Exception {
        storageConfigured();
        when(eventRepository.findLiveImageUrls(any(LocalDate.class))).thenReturn(List.of(BASE + LIVE_KEY));
        when(storageClient.listObjects("events/")).thenReturn(List.of(
            new StoredObject(LIVE_KEY, LONG_AGO), new StoredObject(DEAD_KEY, LONG_AGO)));
    }

    @Test
    void cleanup_deletesTheDeadImagesAndKeepsTheLiveOnes() throws Exception {
        liveEventAndOneDeadImage();

        EventImageCleanupResponse response = service.cleanup(false);

        ArgumentCaptor<List<String>> deleted = ArgumentCaptor.forClass(List.class);
        verify(storageClient).deleteObjects(deleted.capture());
        assertEquals(List.of(DEAD_KEY), deleted.getValue());
        assertFalse(response.dryRun());
        assertEquals(2, response.listedObjects());
        assertEquals(1, response.liveEvents());
        assertEquals(1, response.deadObjects());
        assertEquals(1, response.deletedObjects());
        assertNull(response.abortReason());
    }

    @Test
    void cleanup_dryRunReportsButDeletesNothing() throws Exception {
        liveEventAndOneDeadImage();

        EventImageCleanupResponse response = service.cleanup(true);

        verify(storageClient, never()).deleteObjects(anyList());
        assertTrue(response.dryRun());
        assertEquals(1, response.deadObjects());
        assertEquals(0, response.deletedObjects());
    }

    @Test
    void cleanup_looksUpLiveEventsWithTodayInTheBusinessZone() throws Exception {
        liveEventAndOneDeadImage();

        service.cleanup(true);

        ArgumentCaptor<LocalDate> today = ArgumentCaptor.forClass(LocalDate.class);
        verify(eventRepository).findLiveImageUrls(today.capture());
        assertNotNull(today.getValue());
    }

    @Test
    void cleanup_doesNothingWhenStorageIsNotConfigured() throws Exception {
        when(storageClient.isConfigured()).thenReturn(false);

        EventImageCleanupResponse response = service.cleanup(false);

        verify(storageClient, never()).listObjects(any());
        verify(storageClient, never()).deleteObjects(anyList());
        assertEquals("storage is not configured", response.abortReason());
    }

    @Test
    void cleanup_deletesNothingWhenLiveEventsResolveToNoStorageKey() throws Exception {
        storageConfigured();
        when(eventRepository.findLiveImageUrls(any(LocalDate.class))).thenReturn(List.of("https://example.com/a.jpeg"));
        when(storageClient.listObjects("events/")).thenReturn(List.of(new StoredObject(DEAD_KEY, LONG_AGO)));

        EventImageCleanupResponse response = service.cleanup(false);

        verify(storageClient, never()).deleteObjects(anyList());
        assertNotNull(response.abortReason());
        assertEquals(0, response.deletedObjects());
    }

    @Test
    void cleanup_reportsAListingFailureWithoutDeleting() throws Exception {
        storageConfigured();
        when(eventRepository.findLiveImageUrls(any(LocalDate.class))).thenReturn(List.of(BASE + LIVE_KEY));
        when(storageClient.listObjects("events/")).thenThrow(new IllegalStateException("boom"));

        EventImageCleanupResponse response = service.cleanup(false);

        verify(storageClient, never()).deleteObjects(anyList());
        assertEquals("could not list storage objects", response.abortReason());
    }

    @Test
    void cleanup_reportsADeleteFailureAsNothingDeleted() throws Exception {
        liveEventAndOneDeadImage();
        doThrow(new IllegalStateException("boom")).when(storageClient).deleteObjects(anyList());

        EventImageCleanupResponse response = service.cleanup(false);

        assertEquals(0, response.deletedObjects());
        assertEquals("could not delete storage objects", response.abortReason());
    }

    @Test
    void cleanup_realRunSavesTheTimestampAndDeletedCountOfANewTask() throws Exception {
        liveEventAndOneDeadImage();
        when(taskRunRepository.findByTaskCode("events.cleanup_images")).thenReturn(Optional.empty());

        EventImageCleanupResponse response = service.cleanup(false);

        ArgumentCaptor<MaintenanceTaskRunEntity> saved = ArgumentCaptor.forClass(MaintenanceTaskRunEntity.class);
        verify(taskRunRepository).save(saved.capture());
        assertEquals("events.cleanup_images", saved.getValue().getTaskCode());
        assertEquals(1, saved.getValue().getLastDeletedObjects());
        assertNotNull(saved.getValue().getLastRunAt());
        assertEquals(saved.getValue().getLastRunAt(), response.recordedAt());
    }

    @Test
    void cleanup_realRunUpdatesTheExistingTaskRow() throws Exception {
        liveEventAndOneDeadImage();
        var existing = MaintenanceTaskRunEntity.builder()
            .taskCode("events.cleanup_images").lastRunAt(LocalDateTime.of(2026, 1, 1, 3, 0)).lastDeletedObjects(7).build();
        when(taskRunRepository.findByTaskCode("events.cleanup_images")).thenReturn(Optional.of(existing));

        service.cleanup(false);

        verify(taskRunRepository, times(1)).save(existing);
        assertEquals(1, existing.getLastDeletedObjects());
        assertTrue(existing.getLastRunAt().isAfter(LocalDateTime.of(2026, 1, 1, 3, 0)));
    }

    @Test
    void cleanup_realRunWithNothingDeadStillRecordsTheRun() throws Exception {
        storageConfigured();
        when(eventRepository.findLiveImageUrls(any(LocalDate.class))).thenReturn(List.of(BASE + LIVE_KEY));
        when(storageClient.listObjects("events/")).thenReturn(List.of(new StoredObject(LIVE_KEY, LONG_AGO)));
        when(taskRunRepository.findByTaskCode("events.cleanup_images")).thenReturn(Optional.empty());

        EventImageCleanupResponse response = service.cleanup(false);

        verify(taskRunRepository).save(any(MaintenanceTaskRunEntity.class));
        assertEquals(0, response.deletedObjects());
        assertNotNull(response.recordedAt());
    }

    @Test
    void cleanup_dryRunDoesNotRecordARun() throws Exception {
        liveEventAndOneDeadImage();

        EventImageCleanupResponse response = service.cleanup(true);

        verify(taskRunRepository, never()).save(any(MaintenanceTaskRunEntity.class));
        assertNull(response.recordedAt());
    }

    @Test
    void cleanup_abortedOrFailedRunsDoNotRecordARun() throws Exception {
        storageConfigured();
        when(eventRepository.findLiveImageUrls(any(LocalDate.class))).thenReturn(List.of("https://example.com/a.jpeg"));
        when(storageClient.listObjects("events/")).thenReturn(List.of(new StoredObject(DEAD_KEY, LONG_AGO)));

        service.cleanup(false);

        verify(taskRunRepository, never()).save(any(MaintenanceTaskRunEntity.class));
    }

    @Test
    void lastRun_reportsWhenAndByWhomTheCleanupLastRan() {
        var run = MaintenanceTaskRunEntity.builder()
            .taskCode("events.cleanup_images").lastRunAt(LocalDateTime.of(2026, 10, 12, 9, 30)).lastDeletedObjects(4).build();
        run.setModifiedBy("admin@autocasting.app");
        when(taskRunRepository.findByTaskCode("events.cleanup_images")).thenReturn(Optional.of(run));

        EventImageCleanupStatusResponse status = service.lastRun();

        assertEquals(LocalDateTime.of(2026, 10, 12, 9, 30), status.lastRunAt());
        assertEquals("admin@autocasting.app", status.lastRunBy());
        assertEquals(4, status.lastDeletedObjects());
    }

    @Test
    void lastRun_isEmptyWhenTheCleanupNeverRan() {
        when(taskRunRepository.findByTaskCode(anyString())).thenReturn(Optional.empty());

        EventImageCleanupStatusResponse status = service.lastRun();

        assertNull(status.lastRunAt());
        assertNull(status.lastRunBy());
        assertNull(status.lastDeletedObjects());
    }
}
