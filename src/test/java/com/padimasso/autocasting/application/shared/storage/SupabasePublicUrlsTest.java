package com.padimasso.autocasting.application.shared.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SupabasePublicUrlsTest {

    private static final String BUCKET = "profile-media-public";
    private static final String BASE = "https://x.supabase.co/storage/v1/object/public/" + BUCKET + "/";

    @Test
    void objectKey_returnsThePathInsideTheBucket() {
        assertEquals("events/u-1/1700000000.webp", SupabasePublicUrls.objectKey(BASE + "events/u-1/1700000000.webp", BUCKET));
    }

    @Test
    void objectKey_ignoresQueryAndFragment() {
        assertEquals("events/u-1/a.webp", SupabasePublicUrls.objectKey(BASE + "events/u-1/a.webp?v=2#x", BUCKET));
    }

    @Test
    void objectKey_decodesEncodedCharacters() {
        assertEquals("autocasting/foto uno.jpeg", SupabasePublicUrls.objectKey(BASE + "autocasting/foto%20uno.jpeg", BUCKET));
    }

    @Test
    void objectKey_isNullForAnotherBucket() {
        assertNull(SupabasePublicUrls.objectKey(
            "https://x.supabase.co/storage/v1/object/public/other-bucket/events/u-1/a.webp", BUCKET));
    }

    @Test
    void objectKey_isNullForExternalUrlsAndBlankInput() {
        assertNull(SupabasePublicUrls.objectKey("https://example.com/a.jpeg", BUCKET));
        assertNull(SupabasePublicUrls.objectKey("  ", BUCKET));
        assertNull(SupabasePublicUrls.objectKey(null, BUCKET));
        assertNull(SupabasePublicUrls.objectKey(BASE + "events/u-1/a.webp", null));
    }
}
