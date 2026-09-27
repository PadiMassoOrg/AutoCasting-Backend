package com.padimasso.autocasting.application.castings.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_ARCHIVED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_CLOSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PAUSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CastingAvailabilityTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 9, 26);

    @Test
    void publishedWithDeadlineTodayOrLater_isOpen() {
        assertTrue(CastingAvailability.isOpenToTalents(CASTING_STATUS_PUBLISHED, TODAY, TODAY));
        assertTrue(CastingAvailability.isOpenToTalents(CASTING_STATUS_PUBLISHED, TODAY.plusDays(3), TODAY));
    }

    @Test
    void publishedPastDeadline_isNotOpen() {
        assertFalse(CastingAvailability.isOpenToTalents(CASTING_STATUS_PUBLISHED, TODAY.minusDays(1), TODAY));
    }

    @Test
    void publishedWithoutDeadline_isNotOpen() {
        assertFalse(CastingAvailability.isOpenToTalents(CASTING_STATUS_PUBLISHED, null, TODAY));
    }

    @Test
    void anyOtherStatus_isNotOpenEvenWithAFutureDeadline() {
        for (String status : new String[]{CASTING_STATUS_DRAFT, CASTING_STATUS_PAUSED, CASTING_STATUS_CLOSED, CASTING_STATUS_ARCHIVED, null}) {
            assertFalse(CastingAvailability.isOpenToTalents(status, TODAY.plusDays(3), TODAY), String.valueOf(status));
        }
    }
}
