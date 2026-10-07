package com.padimasso.autocasting.application.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingDetailsResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminCastingMapper;
import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.request.CastingUpsertRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.service.internal.CastingDataApplier;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.dto.HistoryChangeEntry;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CurrencyOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.GenderOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.PayRateTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.RoleTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_ARCHIVED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_CLOSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PAUSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static com.padimasso.autocasting.config.AppConstants.CURRENCY_ARS;
import static com.padimasso.autocasting.config.AppConstants.GENDER_OPTION_INDISTINCT;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_ADMIN_NOT_EDITABLE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTING_ROLE_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_ROLE_MISMATCH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminCastingEditServiceImplTest {

    @Mock
    private CastingRepository castingRepository;
    @Mock
    private CastingRoleRepository castingRoleRepository;
    @Mock
    private SiteMetadataResolver siteMetadataResolver;
    @Mock
    private AdminCastingMapper adminCastingMapper;
    @Mock
    private HistoryService historyService;

    private AdminCastingEditServiceImpl service;
    private UUID castingId;
    private UUID roleId;
    private CastingEntity casting;

    @BeforeEach
    void setUp() {
        var objectMapper = new ObjectMapper().findAndRegisterModules().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        service = new AdminCastingEditServiceImpl(
            castingRepository, castingRoleRepository, new CastingDataApplier(siteMetadataResolver),
            adminCastingMapper, historyService, objectMapper
        );
        castingId = UUID.randomUUID();
        roleId = UUID.randomUUID();
        casting = CastingEntity.builder().id(castingId).title("Old title").status(status(CASTING_STATUS_PUBLISHED)).build();
    }

    private static CastingStatusOptionEntity status(String code) {
        var status = new CastingStatusOptionEntity();
        status.setStringCode(code);
        return status;
    }

    private AdminCastingDetailsResponse details(String title, String location) {
        return new AdminCastingDetailsResponse(
            castingId, "CODE-1", null, "Acme", title, null, null, location, LocalDate.of(2030, 1, 10),
            false, null, null, null, null, List.of(), null, null, null, null, false
        );
    }

    private CastingUpsertRequest castingRequest(String title, String location) {
        return new CastingUpsertRequest(title, null, null, location, LocalDate.of(2030, 1, 10), false, null, null, null, null);
    }

    @Test
    void updateCasting_unknownCasting_throwsNotFoundWithoutHistory() {
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.empty());

        var error = assertThrows(IllegalArgumentException.class,
            () -> service.updateCasting(castingId, new AdminCastingUpdateRequest("r", castingRequest("T", null))));

        assertEquals(CASTINGS_NOT_FOUND, error.getMessage());
        verifyNoInteractions(historyService);
    }

    @Test
    void updateCasting_appliesDataAndRecordsOnlyChangedFieldsWithReason() {
        var before = details("Old title", "Cordoba");
        var after = details("New title", "Cordoba");
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(casting));
        when(adminCastingMapper.toDetailsResponse(casting)).thenReturn(before, after);
        when(castingRepository.saveAndFlush(casting)).thenReturn(casting);

        var result = service.updateCasting(castingId, new AdminCastingUpdateRequest("typo", castingRequest("  New title ", "Cordoba")));

        assertSame(after, result);
        assertEquals("New title", casting.getTitle());
        ArgumentCaptor<Object> changes = ArgumentCaptor.forClass(Object.class);
        verify(historyService).createHistoryEntry(eq(EntityType.CASTING), eq(castingId), eq("typo"), changes.capture());
        @SuppressWarnings("unchecked")
        var entries = (List<HistoryChangeEntry>) changes.getValue();
        assertEquals(1, entries.size());
        assertEquals("casting.title", entries.get(0).fieldKey());
        assertEquals("Old title", entries.get(0).previousValue());
        assertEquals("New title", entries.get(0).newValue());
    }

    @Test
    void updateCasting_withoutRealChange_recordsNoHistory() {
        var same = details("Old title", null);
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(casting));
        when(adminCastingMapper.toDetailsResponse(casting)).thenReturn(same, same);
        when(castingRepository.saveAndFlush(casting)).thenReturn(casting);

        service.updateCasting(castingId, new AdminCastingUpdateRequest("r", castingRequest("Old title", null)));

        verifyNoInteractions(historyService);
    }

    private CastingRoleEntity role(UUID ownerCastingId) {
        var owner = CastingEntity.builder().id(ownerCastingId).status(status(CASTING_STATUS_PAUSED)).build();
        return CastingRoleEntity.builder().id(roleId).casting(owner).roleName("Lead").referencePhotoUrl("https://x/photo.png").build();
    }

    private CastingRoleRequest roleRequest(UUID requestCastingId, String roleName) {
        return new CastingRoleRequest(
            requestCastingId, roleName, UUID.randomUUID(), null, (short) 18, (short) 30, "desc", Set.of(), Set.of(),
            UUID.randomUUID(), null, BigDecimal.TEN, null, false, false, null, null, null, null, null, "https://x/other.png"
        );
    }

    private static final SiteMetadataObject ROLE_TYPE =
        new SiteMetadataObject(UUID.randomUUID(), "sitemetadata.role_type.protagonist", "role_type");

    private CastingRoleResponse roleResponse(String roleName) {
        return new CastingRoleResponse(
            roleId, castingId, roleName, ROLE_TYPE, null, (short) 18, (short) 30, "desc", List.of(), List.of(), null,
            null, null, null, null, false, false, null, "https://x/photo.png", null
        );
    }

    private void stubResolutions() {
        var roleType = new RoleTypeOptionEntity();
        roleType.setStringCode("sitemetadata.role_type.protagonist");
        var gender = new GenderOptionEntity();
        gender.setStringCode(GENDER_OPTION_INDISTINCT);
        var payRate = new PayRateTypeOptionEntity();
        payRate.setStringCode("sitemetadata.pay_rate.per_day");
        var currency = new CurrencyOptionEntity();
        currency.setStringCode(CURRENCY_ARS);
        when(siteMetadataResolver.resolveRoleTypeOrThrow(any())).thenReturn(roleType);
        when(siteMetadataResolver.resolveGenderByCodeOrThrow(GENDER_OPTION_INDISTINCT)).thenReturn(gender);
        when(siteMetadataResolver.resolveProfessionsOrThrow(any())).thenReturn(Set.of());
        when(siteMetadataResolver.resolveSkillsOrThrow(any())).thenReturn(Set.of());
        when(siteMetadataResolver.resolvePayRateTypeOrThrow(any())).thenReturn(payRate);
        lenient().when(siteMetadataResolver.resolveCurrencyByCodeOrThrow(CURRENCY_ARS)).thenReturn(currency);
    }

    @Test
    void updateCastingRole_unknownOrDeletedRole_throwsNotFound() {
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.empty());

        var error = assertThrows(IllegalArgumentException.class,
            () -> service.updateCastingRole(roleId, new AdminCastingRoleUpdateRequest("r", roleRequest(castingId, "A"))));

        assertEquals(CASTING_ROLE_NOT_FOUND, error.getMessage());
        verifyNoInteractions(historyService);
    }

    @Test
    void updateCastingRole_roleOfAnotherCasting_throwsMismatch() {
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(role(UUID.randomUUID())));

        var error = assertThrows(IllegalArgumentException.class,
            () -> service.updateCastingRole(roleId, new AdminCastingRoleUpdateRequest("r", roleRequest(castingId, "A"))));

        assertEquals(CASTINGS_ROLE_MISMATCH, error.getMessage());
        verifyNoInteractions(historyService);
    }

    @Test
    void updateCastingRole_keepsReferencePhotoAndRecordsChangedFields() {
        var entity = role(castingId);
        var before = roleResponse("Lead");
        var after = roleResponse("Villain");
        stubResolutions();
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(entity));
        when(adminCastingMapper.toRoleResponse(entity)).thenReturn(before, after);
        when(castingRoleRepository.saveAndFlush(entity)).thenReturn(entity);

        var result = service.updateCastingRole(roleId, new AdminCastingRoleUpdateRequest("rename", roleRequest(castingId, "Villain")));

        assertSame(after, result);
        assertEquals("Villain", entity.getRoleName());
        assertEquals("https://x/photo.png", entity.getReferencePhotoUrl());
        ArgumentCaptor<Object> changes = ArgumentCaptor.forClass(Object.class);
        verify(historyService).createHistoryEntry(eq(EntityType.CASTING_ROLE), eq(roleId), eq("rename"), changes.capture());
        @SuppressWarnings("unchecked")
        var entries = (List<HistoryChangeEntry>) changes.getValue();
        assertEquals(List.of("role.roleName"), entries.stream().map(HistoryChangeEntry::fieldKey).toList());
        assertTrue(entries.stream().noneMatch(entry -> entry.fieldKey().contains("referencePhotoUrl")));
    }

    @Test
    void updateCasting_closedOrArchivedCasting_isRejectedWithoutChanges() {
        for (String code : List.of(CASTING_STATUS_CLOSED, CASTING_STATUS_ARCHIVED)) {
            casting.setStatus(status(code));
            when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(casting));

            var error = assertThrows(IllegalStateException.class,
                () -> service.updateCasting(castingId, new AdminCastingUpdateRequest("r", castingRequest("Changed", null))));

            assertEquals(CASTINGS_ADMIN_NOT_EDITABLE, error.getMessage());
            assertEquals("Old title", casting.getTitle());
        }
        verifyNoInteractions(historyService);
    }

    @Test
    void updateCastingRole_roleOfClosedCasting_isRejected() {
        var entity = role(castingId);
        entity.getCasting().setStatus(status(CASTING_STATUS_CLOSED));
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(entity));

        var error = assertThrows(IllegalStateException.class,
            () -> service.updateCastingRole(roleId, new AdminCastingRoleUpdateRequest("r", roleRequest(castingId, "Changed"))));

        assertEquals(CASTINGS_ADMIN_NOT_EDITABLE, error.getMessage());
        assertEquals("Lead", entity.getRoleName());
        verifyNoInteractions(historyService);
    }

    @Test
    void updateCasting_draftPublishedAndPausedCastingsAreEditable() {
        for (String code : List.of(CASTING_STATUS_DRAFT, CASTING_STATUS_PUBLISHED, CASTING_STATUS_PAUSED)) {
            casting.setStatus(status(code));
            var same = details("Old title", null);
            when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(casting));
            when(adminCastingMapper.toDetailsResponse(casting)).thenReturn(same, same);
            when(castingRepository.saveAndFlush(casting)).thenReturn(casting);

            service.updateCasting(castingId, new AdminCastingUpdateRequest("r", castingRequest("Old title", null)));
        }
    }
}
