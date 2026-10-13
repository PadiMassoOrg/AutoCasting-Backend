package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.response.AdminEventRowResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminEventMapper;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.events.dto.response.EventDetailsResponse;
import com.padimasso.autocasting.application.events.mapper.EventMapper;
import com.padimasso.autocasting.application.events.model.EventEntity;
import com.padimasso.autocasting.application.events.repository.EventRepository;
import com.padimasso.autocasting.application.sitemetadata.model.EntryAccessOptionEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.EVENTS_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.GENERAL_SLUG_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminEventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    private AdminEventServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AdminEventServiceImpl(eventRepository, new AdminEventMapper(), new EventMapper());
    }

    private static EventEntity event(String slug, LocalDate start, LocalDate end) {
        EntryAccessOptionEntity access = new EntryAccessOptionEntity();
        access.setId(UUID.randomUUID());
        access.setStringCode("sitemetadata.entry_access.free");
        access.setCategoryStringCode("sitemetadata.entry_access");

        return EventEntity.builder()
            .id(UUID.randomUUID())
            .slug(slug)
            .name("Muestra de teatro")
            .entryAccess(access)
            .startDate(start)
            .endDate(end)
            .datesInfo("Lun-Mar 20:30hs")
            .location("Teatro San Martín, Buenos Aires")
            .imageUrl("https://example.com/events/1.jpeg")
            .description("Una muestra")
            .build();
    }

    private static Specification<EventEntity> anySpec() {
        return any();
    }

    private Pageable capturePageable() {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(eventRepository).findAll(anySpec(), captor.capture());
        return captor.getValue();
    }

    @Test
    void listEvents_mapsRowsAndPageMetadata() {
        EventEntity ended = event("E-ENDED01", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2));
        when(eventRepository.findAll(anySpec(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(ended), PageRequest.of(0, 20), 41));

        PageResponse<AdminEventRowResponse> response = service.listEvents(0, 20, null, null, null);

        AdminEventRowResponse row = response.items().getFirst();
        assertEquals(ended.getId(), row.id());
        assertEquals("E-ENDED01", row.slug());
        assertEquals("Muestra de teatro", row.name());
        assertEquals(LocalDate.of(2026, 1, 1), row.startDate());
        assertEquals(LocalDate.of(2026, 1, 2), row.endDate());
        assertEquals(41L, response.totalElements());
        assertEquals(3, response.totalPages());
        assertTrue(response.hasNext());
    }

    @Test
    void listEvents_newestCreatedFirstAndClampsPageAndSize() {
        when(eventRepository.findAll(anySpec(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        service.listEvents(-3, 500, "teatro", null, null);

        Pageable pageable = capturePageable();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(MAX_PAGE_SIZE, pageable.getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "createdAt", "id"), pageable.getSort());
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void listEvents_appliesTheDateWindowToTheSpecification() {
        when(eventRepository.findAll(anySpec(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<EventEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);

        // An inverted window is the only one that is observable without a persistence context: it matches nothing.
        service.listEvents(0, 20, null, LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 12));
        ArgumentCaptor<Specification<EventEntity>> inverted = ArgumentCaptor.forClass(Specification.class);
        verify(eventRepository).findAll(inverted.capture(), any(Pageable.class));
        inverted.getValue().toPredicate(root, query, cb);

        verify(cb).disjunction();
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void listEvents_withoutDatesDoesNotNarrowTheSpecification() {
        when(eventRepository.findAll(anySpec(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<EventEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);

        service.listEvents(0, 20, null, null, null);
        ArgumentCaptor<Specification<EventEntity>> spec = ArgumentCaptor.forClass(Specification.class);
        verify(eventRepository).findAll(spec.capture(), any(Pageable.class));
        spec.getValue().toPredicate(root, query, cb);

        verify(cb, never()).disjunction();
        verify(cb, never()).conjunction();
    }

    @Test
    void getEventDetailsBySlug_returnsEventEvenWhenEnded() {
        EventEntity ended = event("E-ENDED01", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2));
        when(eventRepository.findAdminBySlug(eq("E-ENDED01"))).thenReturn(Optional.of(ended));

        EventDetailsResponse response = service.getEventDetailsBySlug(" E-ENDED01 ");

        assertEquals(ended.getId(), response.id());
        assertEquals("Una muestra", response.description());
        assertEquals("Lun-Mar 20:30hs", response.datesInfo());
        assertEquals("sitemetadata.entry_access.free", response.entryAccess().stringCode());
    }

    @Test
    void getEventDetailsBySlug_unknownSlug_throwsNotFound() {
        when(eventRepository.findAdminBySlug("E-UNKNOWN")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getEventDetailsBySlug("E-UNKNOWN"));

        assertEquals(EVENTS_NOT_FOUND, ex.getMessage());
    }

    @Test
    void getEventDetailsBySlug_blankSlug_throwsSlugRequiredWithoutQuerying() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.getEventDetailsBySlug("  "));

        assertEquals(GENERAL_SLUG_REQUIRED, ex.getMessage());
        verifyNoInteractions(eventRepository);
    }
}
