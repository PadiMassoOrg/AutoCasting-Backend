package com.padimasso.autocasting.application.admin.util;

import org.junit.jupiter.api.Test;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_ARCHIVED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_CLOSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PAUSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminCastingEditabilityTest {

    @Test
    void draftPublishedAndPausedAreEditable() {
        assertTrue(AdminCastingEditability.isEditable(CASTING_STATUS_DRAFT));
        assertTrue(AdminCastingEditability.isEditable(CASTING_STATUS_PUBLISHED));
        assertTrue(AdminCastingEditability.isEditable(CASTING_STATUS_PAUSED));
    }

    @Test
    void closedArchivedAndUnknownAreNotEditable() {
        assertFalse(AdminCastingEditability.isEditable(CASTING_STATUS_CLOSED));
        assertFalse(AdminCastingEditability.isEditable(CASTING_STATUS_ARCHIVED));
        assertFalse(AdminCastingEditability.isEditable("sitemetadata.casting_status.unknown"));
        assertFalse(AdminCastingEditability.isEditable(null));
    }
}
