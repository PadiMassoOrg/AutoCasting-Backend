package com.padimasso.autocasting.application.events.service.impl;

import com.padimasso.autocasting.application.events.dto.EventFilter;
import com.padimasso.autocasting.application.events.dto.response.EventCardResponse;
import com.padimasso.autocasting.application.events.dto.response.EventDetailsResponse;
import com.padimasso.autocasting.application.events.mapper.EventMapper;
import com.padimasso.autocasting.application.sitemetadata.model.EntryAccessOptionEntity;
import com.padimasso.autocasting.application.events.model.EventEntity;
import com.padimasso.autocasting.application.events.repository.EventRepository;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.EVENTS_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.GENERAL_SLUG_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    private EventServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EventServiceImpl(eventRepository, new EventMapper());
    }

    private static EntryAccessOptionEntity access(String code) {
        EntryAccessOptionEntity option = new EntryAccessOptionEntity();
        option.setId(UUID.randomUUID());
        option.setStringCode("sitemetadata.entry_access." + code);
        option.setCategoryStringCode("sitemetadata.entry_access");
        return option;
    }

    private static EventEntity event(String name, EntryAccessOptionEntity access, LocalDate start, LocalDate end) {
        return EventEntity.builder()
            .id(UUID.randomUUID())
            .slug("E-" + name.hashCode())
            .name(name)
            .entryAccess(access)
            .startDate(start)
            .endDate(end)
            .datesInfo("Lun-Mar 20:30hs")
            .location("Teatro San Martín, Buenos Aires")
            .imageUrl("https://example.com/events/" + name.hashCode() + ".jpeg")
            .description("not exposed in the card")
            .build();
    }

    private Pageable capturePageable() {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(eventRepository).findAll(any(Specification.class), captor.capture());
        return captor.getValue();
    }

    @Test
    void search_mapsEventsToCardsAndReportsSliceMetadata() {
        EventEntity first = event("Muestra de teatro", access("free"), LocalDate.of(2026, 10, 12), LocalDate.of(2026, 10, 14));
        EventEntity second = event("Workshop", access("online"), LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 20));
        when(eventRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(first, second), PageRequest.of(0, 2), 5));

        SliceResponse<EventCardResponse> response = service.search(new EventFilter(null, null), 0, 2);

        assertEquals(2, response.items().size());
        assertEquals(first.getId(), response.items().get(0).id());
        assertEquals(first.getSlug(), response.items().get(0).slug());
        assertEquals("Muestra de teatro", response.items().get(0).name());
        assertEquals(second.getSlug(), response.items().get(1).slug());
        assertEquals(LocalDate.of(2026, 10, 12), response.items().get(0).startDate());
        assertEquals(LocalDate.of(2026, 10, 14), response.items().get(0).endDate());
        assertEquals("Lun-Mar 20:30hs", response.items().get(0).datesInfo());
        assertEquals("Teatro San Martín, Buenos Aires", response.items().get(0).location());
        assertEquals(first.getImageUrl(), response.items().get(0).imageUrl());
        assertTrue(response.hasNext());
        assertEquals(0, response.page());
        assertEquals(2, response.size());
        assertEquals(5L, response.totalCount());
    }

    @Test
    void search_lastPage_reportsNoNext() {
        EventEntity only = event("Estreno", access("pay_at_door"), LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 1));
        when(eventRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(only), PageRequest.of(1, 8), 9));

        SliceResponse<EventCardResponse> response = service.search(null, 1, 8);

        assertFalse(response.hasNext());
        assertEquals(1, response.page());
    }

    @Test
    void search_sortsByStartDateAscendingThenId() {
        when(eventRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of()));

        service.search(new EventFilter(null, null), 0, 8);

        Sort sort = capturePageable().getSort();
        assertNotNull(sort.getOrderFor("startDate"));
        assertEquals(Sort.Direction.ASC, sort.getOrderFor("startDate").getDirection());
        assertNotNull(sort.getOrderFor("id"));
    }

    @Test
    void search_clampsPageSizeToMax() {
        when(eventRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of()));

        service.search(new EventFilter(null, null), 0, 500);

        assertEquals(MAX_PAGE_SIZE, capturePageable().getPageSize());
    }

    @Test
    void search_clampsPageSizeToAtLeastOne() {
        when(eventRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of()));

        service.search(new EventFilter(null, null), 0, 0);

        assertEquals(1, capturePageable().getPageSize());
    }

    @Test
    void getBySlug_returnsFullEventDetails() {
        EventEntity event = event("Muestra de teatro", access("online"), LocalDate.of(2026, 10, 12), LocalDate.of(2026, 10, 14));
        event.setTicketUrl("https://example.com/tickets/1");
        when(eventRepository.findPublicBySlug(event.getSlug())).thenReturn(Optional.of(event));

        EventDetailsResponse response = service.getBySlug(" " + event.getSlug() + " ");

        assertEquals(event.getId(), response.id());
        assertEquals(event.getSlug(), response.slug());
        assertEquals(event.getEntryAccess().getId(), response.entryAccess().id());
        assertEquals("sitemetadata.entry_access.online", response.entryAccess().stringCode());
        assertEquals("https://example.com/tickets/1", response.ticketUrl());
        assertEquals("not exposed in the card", response.description());
        assertEquals(event.getImageUrl(), response.imageUrl());
        assertEquals(event.getLocation(), response.location());
    }

    @Test
    void getBySlug_unknownSlug_throwsNotFound() {
        when(eventRepository.findPublicBySlug("E-UNKNOWN")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getBySlug("E-UNKNOWN"));

        assertEquals(EVENTS_NOT_FOUND, ex.getMessage());
    }

    @Test
    void getBySlug_blankSlug_throwsSlugRequired() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getBySlug("  "));

        assertEquals(GENERAL_SLUG_REQUIRED, ex.getMessage());
    }
}
