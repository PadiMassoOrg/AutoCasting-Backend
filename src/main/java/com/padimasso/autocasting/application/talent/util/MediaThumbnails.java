package com.padimasso.autocasting.application.talent.util;

import java.util.List;

/**
 * Listing thumbnails are stored next to their original photo under the original object key
 * plus {@link #THUMBNAIL_SUFFIX}. The Frontend follows the same convention
 * ({@code integrations/supabase/media/lib/thumbnail.ts}), so the thumbnail is derived, never stored.
 */
public final class MediaThumbnails {

    public static final String THUMBNAIL_SUFFIX = ".thumb.webp";

    private MediaThumbnails() {
    }

    public static String thumbnailKeyOf(String objectKey) {
        return objectKey + THUMBNAIL_SUFFIX;
    }

    public static List<String> withThumbnail(String objectKey) {
        return List.of(objectKey, thumbnailKeyOf(objectKey));
    }
}
