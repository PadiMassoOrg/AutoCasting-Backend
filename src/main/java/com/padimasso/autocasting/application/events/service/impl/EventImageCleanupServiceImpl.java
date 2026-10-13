package com.padimasso.autocasting.application.events.service.impl;

import com.padimasso.autocasting.application.castings.util.CastingDeadlines;
import com.padimasso.autocasting.application.common.model.MaintenanceTaskRunEntity;
import com.padimasso.autocasting.application.common.repository.MaintenanceTaskRunRepository;
import com.padimasso.autocasting.application.events.dto.response.EventImageCleanupResponse;
import com.padimasso.autocasting.application.events.dto.response.EventImageCleanupStatusResponse;
import com.padimasso.autocasting.application.events.repository.EventRepository;
import com.padimasso.autocasting.application.events.service.EventImageCleanupService;
import com.padimasso.autocasting.application.events.util.EventImageGarbage;
import com.padimasso.autocasting.application.shared.storage.StoredObject;
import com.padimasso.autocasting.application.shared.storage.SupabasePublicUrls;
import com.padimasso.autocasting.application.shared.storage.SupabaseStorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.padimasso.autocasting.config.AppConstants.MAINTENANCE_TASK_EVENT_IMAGES_CLEANUP;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventImageCleanupServiceImpl implements EventImageCleanupService {

    static final Duration GRACE_PERIOD = Duration.ofHours(24);
    static final int MAX_DELETIONS_PER_RUN = 200;

    private final EventRepository eventRepository;
    private final SupabaseStorageClient storageClient;
    private final MaintenanceTaskRunRepository taskRunRepository;

    @Override
    public EventImageCleanupResponse cleanup(boolean dryRun) {
        if (!storageClient.isConfigured()) {
            log.warn("Supabase media storage is not configured; skipping event image cleanup");
            return new EventImageCleanupResponse(dryRun, 0, 0, 0, 0, 0, "storage is not configured", null);
        }

        LocalDate today = CastingDeadlines.today();
        List<String> liveImageUrls = eventRepository.findLiveImageUrls(today);
        Set<String> referenced = EventImageGarbage.referencedKeys(
            liveImageUrls.stream()
                .map(url -> SupabasePublicUrls.objectKey(url, storageClient.mediaBucket()))
                .filter(Objects::nonNull)
                .toList()
        );

        List<StoredObject> objects;
        try {
            objects = storageClient.listObjects(EventImageGarbage.USER_IMAGES_PREFIX);
        } catch (Exception ex) {
            log.warn("Could not list event images in Supabase Storage", ex);
            return new EventImageCleanupResponse(dryRun, 0, 0, liveImageUrls.size(), 0, 0, "could not list storage objects", null);
        }

        var plan = EventImageGarbage.plan(
            objects, referenced, liveImageUrls.size(), Instant.now(), GRACE_PERIOD, MAX_DELETIONS_PER_RUN
        );

        if (plan.aborted()) {
            log.warn("Event image cleanup aborted: {}", plan.abortReason());
            return response(dryRun, plan, liveImageUrls.size(), 0, null);
        }

        int deleted = 0;
        if (!dryRun && !plan.keysToDelete().isEmpty()) {
            try {
                storageClient.deleteObjects(plan.keysToDelete());
                deleted = plan.keysToDelete().size();
            } catch (Exception ex) {
                log.warn("Could not delete {} dead event images from Supabase Storage", plan.keysToDelete().size(), ex);
                return new EventImageCleanupResponse(
                    dryRun, plan.listed(), plan.candidates(), liveImageUrls.size(),
                    plan.keysToDelete().size(), 0, "could not delete storage objects", null
                );
            }
        }

        LocalDateTime recordedAt = dryRun ? null : recordRun(deleted);

        log.info("Event image cleanup done. dryRun={}, listed={}, candidates={}, liveEvents={}, dead={}, deleted={}",
            dryRun, plan.listed(), plan.candidates(), liveImageUrls.size(), plan.keysToDelete().size(), deleted);
        return response(dryRun, plan, liveImageUrls.size(), deleted, recordedAt);
    }

    @Override
    public EventImageCleanupStatusResponse lastRun() {
        return taskRunRepository.findByTaskCode(MAINTENANCE_TASK_EVENT_IMAGES_CLEANUP)
            .map(run -> new EventImageCleanupStatusResponse(run.getLastRunAt(), run.getModifiedBy(), run.getLastDeletedObjects()))
            .orElse(new EventImageCleanupStatusResponse(null, null, null));
    }

    private LocalDateTime recordRun(int deleted) {
        LocalDateTime now = LocalDateTime.now();
        var run = taskRunRepository.findByTaskCode(MAINTENANCE_TASK_EVENT_IMAGES_CLEANUP)
            .orElseGet(() -> MaintenanceTaskRunEntity.builder().taskCode(MAINTENANCE_TASK_EVENT_IMAGES_CLEANUP).build());
        run.setLastRunAt(now);
        run.setLastDeletedObjects(deleted);
        taskRunRepository.save(run);
        return now;
    }

    private static EventImageCleanupResponse response(
        boolean dryRun, EventImageGarbage.Plan plan, int liveEvents, int deleted, LocalDateTime recordedAt
    ) {
        return new EventImageCleanupResponse(
            dryRun, plan.listed(), plan.candidates(), liveEvents, plan.keysToDelete().size(), deleted,
            plan.abortReason(), recordedAt
        );
    }
}
