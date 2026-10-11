package com.padimasso.autocasting.application.events.util;

import java.time.LocalDate;

// The public catalog lists events that have not ended yet: without an explicit dateFrom the window
// opens today. An explicit dateFrom is honoured as-is, so callers may still look at past events.
public final class EventDateWindow {

    private EventDateWindow() {
    }

    public static LocalDate effectiveFrom(LocalDate dateFrom, LocalDate today) {
        return dateFrom != null ? dateFrom : today;
    }
}
