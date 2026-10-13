package com.padimasso.autocasting.application.events.util;

import java.time.LocalDate;

// Events that already ended do not exist for anyone: the public window never opens before today, even
// when the caller asks for an earlier dateFrom.
public final class EventDateWindow {

    private EventDateWindow() {
    }

    public static LocalDate effectiveFrom(LocalDate dateFrom, LocalDate today) {
        return dateFrom == null || dateFrom.isBefore(today) ? today : dateFrom;
    }
}
