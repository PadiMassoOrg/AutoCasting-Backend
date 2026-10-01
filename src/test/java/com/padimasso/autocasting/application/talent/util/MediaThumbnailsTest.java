package com.padimasso.autocasting.application.talent.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MediaThumbnailsTest {

    @Test
    void thumbnailKeyOf_appendsSuffixToFullKey() {
        assertEquals(
            "talent/abc/media/headshot/1727000000000.webp.thumb.webp",
            MediaThumbnails.thumbnailKeyOf("talent/abc/media/headshot/1727000000000.webp")
        );
    }

    @Test
    void thumbnailKeyOf_keepsLegacyExtension() {
        assertEquals(
            "talent/abc/media/other/1700000000000.jpg.thumb.webp",
            MediaThumbnails.thumbnailKeyOf("talent/abc/media/other/1700000000000.jpg")
        );
    }

    @Test
    void thumbnailKeyOf_keepsSpacesAndSpecialCharacters() {
        assertEquals("talent/a b/foto ñ.webp.thumb.webp", MediaThumbnails.thumbnailKeyOf("talent/a b/foto ñ.webp"));
    }

    @Test
    void withThumbnail_returnsOriginalThenThumbnail() {
        assertEquals(
            List.of("castings/x/role.webp", "castings/x/role.webp.thumb.webp"),
            MediaThumbnails.withThumbnail("castings/x/role.webp")
        );
    }
}
