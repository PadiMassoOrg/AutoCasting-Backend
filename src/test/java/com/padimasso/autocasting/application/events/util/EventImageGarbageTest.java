package com.padimasso.autocasting.application.events.util;

import com.padimasso.autocasting.application.shared.storage.StoredObject;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventImageGarbageTest {

    private static final Instant NOW = Instant.parse("2026-10-12T12:00:00Z");
    private static final Duration GRACE = Duration.ofHours(24);
    private static final String USER = "0b4e8f0e-1111-4222-8333-444455556666";
    private static final String OLD = "events/" + USER + "/1700000000.webp";
    private static final String OLD_THUMB = OLD + ".thumb.webp";
    private static final String LIVE = "events/" + USER + "/1700000001.webp";
    private static final Instant LONG_AGO = NOW.minus(Duration.ofDays(30));

    private static StoredObject stored(String key, Instant createdAt) {
        return new StoredObject(key, createdAt);
    }

    @Test
    void isUserEventImageKey_acceptsOnlyFilesDirectlyInsideAUserFolder() {
        assertTrue(EventImageGarbage.isUserEventImageKey(OLD));
        assertTrue(EventImageGarbage.isUserEventImageKey(OLD_THUMB));
        assertFalse(EventImageGarbage.isUserEventImageKey("autocasting/events/placeholder.webp"));
        assertFalse(EventImageGarbage.isUserEventImageKey("autocasting/A4351A42.jpeg"));
        assertFalse(EventImageGarbage.isUserEventImageKey("events/placeholders/theatre.webp"));
        assertFalse(EventImageGarbage.isUserEventImageKey("events/" + USER + "/nested/a.webp"));
        assertFalse(EventImageGarbage.isUserEventImageKey("events/" + USER + "/"));
        assertFalse(EventImageGarbage.isUserEventImageKey("employer/" + USER + "/logo/1.webp"));
        assertFalse(EventImageGarbage.isUserEventImageKey("talent/" + USER + "/media/headshot/1.webp"));
        assertFalse(EventImageGarbage.isUserEventImageKey(null));
    }

    @Test
    void referencedKeys_keepsEachImageAndItsThumbnail() {
        assertEquals(Set.of(LIVE, LIVE + ".thumb.webp"), EventImageGarbage.referencedKeys(List.of(LIVE)));
    }

    @Test
    void plan_deletesUnreferencedOldImagesAndTheirThumbnails() {
        var plan = EventImageGarbage.plan(
            List.of(stored(OLD, LONG_AGO), stored(OLD_THUMB, LONG_AGO), stored(LIVE, LONG_AGO)),
            EventImageGarbage.referencedKeys(List.of(LIVE)), 1, NOW, GRACE, 200);

        assertFalse(plan.aborted());
        assertEquals(List.of(OLD, OLD_THUMB), plan.keysToDelete());
        assertEquals(3, plan.listed());
        assertEquals(3, plan.candidates());
    }

    @Test
    void plan_keepsReferencedImagesAndTheirThumbnails() {
        var plan = EventImageGarbage.plan(
            List.of(stored(LIVE, LONG_AGO), stored(LIVE + ".thumb.webp", LONG_AGO)),
            EventImageGarbage.referencedKeys(List.of(LIVE)), 1, NOW, GRACE, 200);

        assertTrue(plan.keysToDelete().isEmpty());
    }

    @Test
    void plan_keepsRecentUploadsInsideTheGracePeriod() {
        var plan = EventImageGarbage.plan(
            List.of(stored(OLD, NOW.minus(Duration.ofHours(2)))),
            Set.of(LIVE), 1, NOW, GRACE, 200);

        assertTrue(plan.keysToDelete().isEmpty());
    }

    @Test
    void plan_keepsObjectsWithoutACreationTime() {
        var plan = EventImageGarbage.plan(List.of(stored(OLD, null)), Set.of(LIVE), 1, NOW, GRACE, 200);

        assertTrue(plan.keysToDelete().isEmpty());
    }

    @Test
    void plan_neverTouchesObjectsOutsideTheUserEventFolders() {
        var plan = EventImageGarbage.plan(
            List.of(
                stored("autocasting/events/placeholder.webp", LONG_AGO),
                stored("autocasting/A4351A42.jpeg", LONG_AGO),
                stored("employer/" + USER + "/logo/1.webp", LONG_AGO),
                stored("events/stray.webp", LONG_AGO)),
            Set.of(LIVE), 1, NOW, GRACE, 200);

        assertTrue(plan.keysToDelete().isEmpty());
        assertEquals(0, plan.candidates());
        assertEquals(4, plan.listed());
    }

    @Test
    void plan_abortsWhenLiveEventsExistButNoneResolvedToAKey() {
        var plan = EventImageGarbage.plan(List.of(stored(OLD, LONG_AGO)), Set.of(), 5, NOW, GRACE, 200);

        assertTrue(plan.aborted());
        assertTrue(plan.keysToDelete().isEmpty());
    }

    @Test
    void plan_withNoLiveEventsDeletesEveryOldCandidate() {
        var plan = EventImageGarbage.plan(List.of(stored(OLD, LONG_AGO)), Set.of(), 0, NOW, GRACE, 200);

        assertFalse(plan.aborted());
        assertEquals(List.of(OLD), plan.keysToDelete());
    }

    @Test
    void plan_abortsWhenItWouldDeleteMoreThanTheLimit() {
        var plan = EventImageGarbage.plan(
            List.of(stored(OLD, LONG_AGO), stored(OLD_THUMB, LONG_AGO)), Set.of(LIVE), 1, NOW, GRACE, 1);

        assertTrue(plan.aborted());
        assertTrue(plan.keysToDelete().isEmpty());
    }

    @Test
    void plan_isNotAbortedWhenExactlyAtTheLimit() {
        var plan = EventImageGarbage.plan(
            List.of(stored(OLD, LONG_AGO), stored(OLD_THUMB, LONG_AGO)), Set.of(LIVE), 1, NOW, GRACE, 2);

        assertNull(plan.abortReason());
        assertEquals(2, plan.keysToDelete().size());
    }
}
