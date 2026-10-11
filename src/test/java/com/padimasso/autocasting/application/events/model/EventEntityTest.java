package com.padimasso.autocasting.application.events.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventEntityTest {

    private static final String SLUG_FORMAT = "E-[0-9A-F]{8}";

    @Test
    void generateSlug_withoutSlug_createsEPrefixedUppercaseCode() {
        EventEntity event = new EventEntity();

        event.generateSlug();

        assertTrue(event.getSlug().matches(SLUG_FORMAT));
    }

    @Test
    void generateSlug_withBlankSlug_createsOne() {
        EventEntity event = EventEntity.builder().slug("  ").build();

        event.generateSlug();

        assertTrue(event.getSlug().matches(SLUG_FORMAT));
    }

    @Test
    void generateSlug_withExistingSlug_keepsIt() {
        EventEntity event = EventEntity.builder().slug("E-DEMO-01").build();

        event.generateSlug();

        assertEquals("E-DEMO-01", event.getSlug());
    }

    @Test
    void generateSlug_producesDifferentCodesForDifferentEvents() {
        EventEntity first = new EventEntity();
        EventEntity second = new EventEntity();

        first.generateSlug();
        second.generateSlug();

        assertNotEquals(first.getSlug(), second.getSlug());
    }
}
