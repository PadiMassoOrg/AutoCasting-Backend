package com.padimasso.autocasting.application.castings.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CastingDeadlinesTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 9, 26);

    @AfterEach
    void restoreDefaultZone() {
        CastingDeadlines.configureZone(ZoneId.of(CastingDeadlines.DEFAULT_ZONE_ID));
    }

    @Test
    void defaultZone_isBuenosAires() {
        assertEquals(ZoneId.of("America/Argentina/Buenos_Aires"), CastingDeadlines.zone());
    }

    @Test
    void configuredZone_drivesToday() {
        // UTC+14 and UTC-11 are always on different calendar days.
        CastingDeadlines.configureZone(ZoneId.of("Pacific/Kiritimati"));
        LocalDate farEast = CastingDeadlines.today();
        CastingDeadlines.configureZone(ZoneId.of("Pacific/Pago_Pago"));
        LocalDate farWest = CastingDeadlines.today();

        assertNotEquals(farEast, farWest);
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
