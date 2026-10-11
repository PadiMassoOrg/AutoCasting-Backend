package com.padimasso.autocasting.application.events.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventDateWindowTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 10);

    @Test
    void effectiveFrom_withoutDateFrom_opensWindowToday() {
        assertEquals(TODAY, EventDateWindow.effectiveFrom(null, TODAY));
    }

    @Test
    void effectiveFrom_withFutureDateFrom_usesDateFrom() {
        LocalDate dateFrom = TODAY.plusDays(5);

        assertEquals(dateFrom, EventDateWindow.effectiveFrom(dateFrom, TODAY));
    }

    @Test
    void effectiveFrom_withPastDateFrom_honoursDateFrom() {
        LocalDate dateFrom = TODAY.minusDays(30);

        assertEquals(dateFrom, EventDateWindow.effectiveFrom(dateFrom, TODAY));
    }
}
