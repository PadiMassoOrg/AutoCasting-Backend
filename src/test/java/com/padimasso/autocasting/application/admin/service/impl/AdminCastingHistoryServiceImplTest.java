package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.mapper.AdminHistoryMapper;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.repository.projection.CastingRoleKeyProjection;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.model.HistoryEntity;
import com.padimasso.autocasting.application.history.service.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminCastingHistoryServiceImplTest {

    @Mock
    private CastingRepository castingRepository;
    @Mock
    private CastingRoleRepository castingRoleRepository;
    @Mock
    private HistoryService historyService;

    private AdminCastingHistoryServiceImpl service;
    private final UUID castingId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new AdminCastingHistoryServiceImpl(castingRepository, castingRoleRepository, historyService, new AdminHistoryMapper());
    }

    private static CastingRoleKeyProjection roleKey(UUID id, String name) {
        return new CastingRoleKeyProjection() {
            @Override
            public UUID getRoleId() {
                return id;
            }

            @Override
            public String getRoleName() {
                return name;
            }
        };
    }

    private static HistoryEntity entry(EntityType type, UUID entityId, String note) {
        return HistoryEntity.builder().id(UUID.randomUUID()).entityType(type).entityId(entityId).note(note).changes("[]").build();
    }

    @Test
    void unknownCasting_isNotFound() {
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.empty());

        var error = assertThrows(IllegalArgumentException.class, () -> service.listCastingHistory(castingId, 0, 20));

        assertEquals(CASTINGS_NOT_FOUND, error.getMessage());
        verifyNoInteractions(historyService);
    }

    @Test
    void roleEntriesCarryTheRoleNameIncludingDeletedRolesAndCastingEntriesDoNot() {
        var liveRole = UUID.randomUUID();
        var deletedRole = UUID.randomUUID();
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(CastingEntity.builder().id(castingId).build()));
        when(castingRoleRepository.findAllRoleKeysIncludingDeletedByCastingId(castingId))
            .thenReturn(List.of(roleKey(liveRole, "Lead"), roleKey(deletedRole, "Removed role")));
        var rows = List.of(
            entry(EntityType.CASTING, castingId, "typo"),
            entry(EntityType.CASTING_ROLE, liveRole, "rename"),
            entry(EntityType.CASTING_ROLE, deletedRole, "wrong role")
        );
        when(historyService.listHistoryByEntityAndRelated(eq(EntityType.CASTING), eq(castingId), eq(EntityType.CASTING_ROLE), any(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(rows));

        var result = service.listCastingHistory(castingId, 0, 20);

        assertEquals(3, result.items().size());
        assertNull(result.items().get(0).entityLabel());
        assertEquals("Lead", result.items().get(1).entityLabel());
        assertEquals("Removed role", result.items().get(2).entityLabel());
        assertEquals("typo", result.items().get(0).note());
        ArgumentCaptor<Collection<UUID>> roleIds = ArgumentCaptor.forClass(Collection.class);
        verify(historyService).listHistoryByEntityAndRelated(any(), any(), any(), roleIds.capture(), any());
        assertEquals(Set.of(liveRole, deletedRole), Set.copyOf(roleIds.getValue()));
    }

    @Test
    void pageRequestIsNormalizedAndSortedNewestFirst() {
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(CastingEntity.builder().id(castingId).build()));
        when(castingRoleRepository.findAllRoleKeysIncludingDeletedByCastingId(castingId)).thenReturn(List.of());
        when(historyService.listHistoryByEntityAndRelated(any(), any(), any(), any(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(), org.springframework.data.domain.PageRequest.of(0, 1), 0));

        service.listCastingHistory(castingId, -3, 100_000);

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(historyService).listHistoryByEntityAndRelated(any(), any(), any(), any(), pageable.capture());
        assertEquals(0, pageable.getValue().getPageNumber());
        assertEquals(MAX_PAGE_SIZE, pageable.getValue().getPageSize());
        assertEquals("createdAt: DESC,id: DESC", pageable.getValue().getSort().toString());
    }
}
