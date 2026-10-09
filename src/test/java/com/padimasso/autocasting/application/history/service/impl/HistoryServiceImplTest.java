package com.padimasso.autocasting.application.history.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.repository.HistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HistoryServiceImplTest {

    @Mock
    private HistoryRepository historyRepository;

    @Test
    void relatedIdsArePassedThrough() {
        var service = new HistoryServiceImpl(historyRepository, new ObjectMapper());
        var castingId = UUID.randomUUID();
        var roleIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        var pageable = PageRequest.of(0, 10);

        service.listHistoryByEntityAndRelated(EntityType.CASTING, castingId, EntityType.CASTING_ROLE, roleIds, pageable);

        verify(historyRepository).findAllByEntityOrRelatedEntities(EntityType.CASTING, castingId, EntityType.CASTING_ROLE, roleIds, pageable);
    }

    @Test
    void noRelatedEntitiesNeverSendsAnEmptyInList() {
        var service = new HistoryServiceImpl(historyRepository, new ObjectMapper());
        var castingId = UUID.randomUUID();

        service.listHistoryByEntityAndRelated(EntityType.CASTING, castingId, EntityType.CASTING_ROLE, List.of(), PageRequest.of(0, 10));

        ArgumentCaptor<Collection<UUID>> ids = ArgumentCaptor.forClass(Collection.class);
        verify(historyRepository).findAllByEntityOrRelatedEntities(eq(EntityType.CASTING), eq(castingId), eq(EntityType.CASTING_ROLE), ids.capture(), any());
        assertEquals(1, ids.getValue().size());
        assertFalse(ids.getValue().isEmpty());
    }
}
