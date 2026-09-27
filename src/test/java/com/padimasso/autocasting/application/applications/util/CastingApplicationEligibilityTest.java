package com.padimasso.autocasting.application.applications.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_ARCHIVED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_CLOSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PAUSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_DEADLINE_PASSED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CastingApplicationEligibilityTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 9, 26);

    @Test
    void publishedWithFutureDeadline_isAllowed() {
        assertEquals(Optional.empty(), CastingApplicationEligibility.rejectionReason(CASTING_STATUS_PUBLISHED, TODAY.plusDays(1), TODAY));
    }

    @Test
    void publishedWithDeadlineToday_isAllowedUntilTheDayEnds() {
        assertEquals(Optional.empty(), CastingApplicationEligibility.rejectionReason(CASTING_STATUS_PUBLISHED, TODAY, TODAY));
    }

    @Test
    void publishedWithPastDeadline_isRejectedEvenIfTheJobHasNotClosedItYet() {
        assertEquals(Optional.of(CASTINGS_DEADLINE_PASSED), CastingApplicationEligibility.rejectionReason(CASTING_STATUS_PUBLISHED, TODAY.minusDays(3), TODAY));
    }

    @Test
    void closedOrArchived_isRejectedAsFinished() {
        assertEquals(Optional.of(CASTINGS_DEADLINE_PASSED), CastingApplicationEligibility.rejectionReason(CASTING_STATUS_CLOSED, TODAY.plusDays(5), TODAY));
        assertEquals(Optional.of(CASTINGS_DEADLINE_PASSED), CastingApplicationEligibility.rejectionReason(CASTING_STATUS_ARCHIVED, TODAY.plusDays(5), TODAY));
    }

    @Test
    void draftOrPaused_isRejectedAsNotFound() {
        assertEquals(Optional.of(CASTINGS_NOT_FOUND), CastingApplicationEligibility.rejectionReason(CASTING_STATUS_DRAFT, TODAY.plusDays(5), TODAY));
        assertEquals(Optional.of(CASTINGS_NOT_FOUND), CastingApplicationEligibility.rejectionReason(CASTING_STATUS_PAUSED, TODAY.plusDays(5), TODAY));
    }
}
