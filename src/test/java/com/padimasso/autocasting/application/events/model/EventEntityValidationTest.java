package com.padimasso.autocasting.application.events.model;

import com.padimasso.autocasting.application.sitemetadata.model.EntryAccessOptionEntity;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventEntityValidationTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private static final LocalDate START = LocalDate.of(2026, 10, 12);
    private static final LocalDate END = LocalDate.of(2026, 10, 14);

    private static EntryAccessOptionEntity access(String code) {
        EntryAccessOptionEntity option = new EntryAccessOptionEntity();
        option.setId(UUID.randomUUID());
        option.setStringCode("sitemetadata.entry_access." + code);
        return option;
    }

    private static EventEntity.EventEntityBuilder valid() {
        return EventEntity.builder()
            .name("Muestra de teatro")
            .entryAccess(access("free"))
            .startDate(START)
            .endDate(END)
            .location("Teatro del Abasto, Buenos Aires")
            .imageUrl("https://example.com/events/image.jpeg");
    }

    private static List<String> violations(EventEntity event) {
        return VALIDATOR.validate(event).stream().map(v -> v.getMessage()).sorted().toList();
    }

    @Test
    void validEvent_hasNoViolations() {
        assertTrue(violations(valid().build()).isEmpty());
    }

    @Test
    void datesInfoAndDescription_areOptional() {
        assertTrue(violations(valid().datesInfo(null).description(null).build()).isEmpty());
    }

    @Test
    void name_blank_isRejected() {
        assertEquals(List.of("events.name.required"), violations(valid().name("  ").build()));
    }

    @Test
    void name_missing_isRejected() {
        assertEquals(List.of("events.name.required"), violations(valid().name(null).build()));
    }

    @Test
    void entryAccess_missing_isRejected() {
        assertEquals(List.of("events.entry_access.required"), violations(valid().entryAccess(null).build()));
    }

    @Test
    void startDate_missing_isRejected() {
        assertEquals(List.of("events.start_date.required"), violations(valid().startDate(null).build()));
    }

    @Test
    void endDate_missing_isRejected() {
        assertEquals(List.of("events.end_date.required"), violations(valid().endDate(null).build()));
    }

    @Test
    void location_blank_isRejected() {
        assertEquals(List.of("events.location.required"), violations(valid().location(" ").build()));
    }

    @Test
    void location_missing_isRejected() {
        assertEquals(List.of("events.location.required"), violations(valid().location(null).build()));
    }

    @Test
    void imageUrl_blank_isRejected() {
        assertEquals(List.of("events.image_url.required"), violations(valid().imageUrl(" ").build()));
    }

    @Test
    void imageUrl_missing_isRejected() {
        assertEquals(List.of("events.image_url.required"), violations(valid().imageUrl(null).build()));
    }

    @Test
    void endDate_beforeStartDate_isRejected() {
        assertEquals(List.of("events.date_range.invalid"), violations(valid().startDate(END).endDate(START).build()));
    }

    @Test
    void endDate_sameAsStartDate_isAccepted() {
        assertTrue(violations(valid().startDate(START).endDate(START).build()).isEmpty());
    }

    @Test
    void onlineAccess_withoutTicketUrl_isRejected() {
        assertEquals(List.of("events.ticket_url.online_only_and_required"),
            violations(valid().entryAccess(access("online")).ticketUrl(null).build()));
    }

    @Test
    void onlineAccess_withBlankTicketUrl_isRejected() {
        assertEquals(List.of("events.ticket_url.online_only_and_required"),
            violations(valid().entryAccess(access("online")).ticketUrl("  ").build()));
    }

    @Test
    void onlineAccess_withTicketUrl_isAccepted() {
        assertTrue(violations(valid().entryAccess(access("online")).ticketUrl("https://example.com/tickets/1").build()).isEmpty());
    }

    @Test
    void freeAccess_withTicketUrl_isRejected() {
        assertEquals(List.of("events.ticket_url.online_only_and_required"),
            violations(valid().entryAccess(access("free")).ticketUrl("https://example.com/tickets/1").build()));
    }

    @Test
    void payAtDoorAccess_withTicketUrl_isRejected() {
        assertEquals(List.of("events.ticket_url.online_only_and_required"),
            violations(valid().entryAccess(access("pay_at_door")).ticketUrl("https://example.com/tickets/1").build()));
    }
}
