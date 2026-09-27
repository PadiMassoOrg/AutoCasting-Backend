package com.padimasso.autocasting.application.castings.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CastingDeadlinesTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 9, 26);

    @Test
    void zone_isArgentina() {
        assertEquals(ZoneId.of("America/Argentina/Buenos_Aires"), CastingDeadlines.ZONE);
        assertEquals(CastingDeadlines.ZONE, ZoneId.of(CastingDeadlines.ZONE_ID));
    }

    @Test
    void deadlineAfterToday_isOpen() {
        assertTrue(CastingDeadlines.isOpen(TODAY.plusDays(1), TODAY));
        assertFalse(CastingDeadlines.hasPassed(TODAY.plusDays(1), TODAY));
    }

    @Test
    void deadlineToday_isStillOpen() {
        assertTrue(CastingDeadlines.isOpen(TODAY, TODAY));
        assertFalse(CastingDeadlines.hasPassed(TODAY, TODAY));
    }

    @Test
    void deadlineBeforeToday_hasPassed() {
        assertFalse(CastingDeadlines.isOpen(TODAY.minusDays(1), TODAY));
        assertTrue(CastingDeadlines.hasPassed(TODAY.minusDays(1), TODAY));
    }

    @Test
    void missingDeadline_isNeitherOpenNorPassed() {
        assertFalse(CastingDeadlines.isOpen(null, TODAY));
        assertFalse(CastingDeadlines.hasPassed(null, TODAY));
    }
}
