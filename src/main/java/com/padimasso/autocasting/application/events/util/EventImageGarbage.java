package com.padimasso.autocasting.application.events.util;

import com.padimasso.autocasting.application.shared.storage.StoredObject;
import com.padimasso.autocasting.application.talent.util.MediaThumbnails;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Decides which uploaded event images are dead: an image is dead when no live event references it. Only files
 * directly inside {@code events/<userId>/} are ever candidates (an allowlist), so placeholders and any other
 * folder are unreachable by construction.
 */
public final class EventImageGarbage {

    public static final String USER_IMAGES_PREFIX = "events/";

    private static final Pattern USER_EVENT_IMAGE =
        Pattern.compile("^events/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/[^/]+$");

    private EventImageGarbage() {
    }

    public record Plan(int listed, int candidates, List<String> keysToDelete, String abortReason) {
        public boolean aborted() {
            return abortReason != null;
        }
    }

    public static boolean isUserEventImageKey(String key) {
        return key != null && USER_EVENT_IMAGE.matcher(key).matches();
    }

    // Keys kept alive by the live events: each image and its listing thumbnail.
    public static Set<String> referencedKeys(Collection<String> liveImageKeys) {
        Set<String> keys = new HashSet<>();
        for (String key : liveImageKeys) {
            keys.addAll(MediaThumbnails.withThumbnail(key));
        }
        return keys;
    }

    public static Plan plan(
        List<StoredObject> objects,
        Set<String> referencedKeys,
        int liveEventCount,
        Instant now,
        Duration grace,
        int maxDeletions
    ) {
        List<StoredObject> candidates = objects.stream().filter(o -> isUserEventImageKey(o.key())).toList();

        // Live events exist but none resolved to a storage key: the lookup is broken, so delete nothing.
        if (liveEventCount > 0 && referencedKeys.isEmpty()) {
            return new Plan(objects.size(), candidates.size(), List.of(), "no live event image resolved to a storage key");
        }

        Instant cutoff = now.minus(grace);
        List<String> dead = candidates.stream()
            .filter(o -> !referencedKeys.contains(o.key()))
            // Unknown creation time or inside the grace period (an upload not yet attached to an event): keep.
            .filter(o -> o.createdAt() != null && o.createdAt().isBefore(cutoff))
            .map(StoredObject::key)
            .toList();

        if (dead.size() > maxDeletions) {
            return new Plan(objects.size(), candidates.size(), List.of(),
                "would delete " + dead.size() + " objects, above the limit of " + maxDeletions);
        }

        return new Plan(objects.size(), candidates.size(), dead, null);
    }
}
