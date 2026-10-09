package com.padimasso.autocasting.application.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingDetailsResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminCastingMapper;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleCreateRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleDeleteRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleDuplicateRequest;
import com.padimasso.autocasting.application.applications.repository.CastingApplicationRepository;
import com.padimasso.autocasting.application.applications.repository.projection.ApplicationStatusCountProjection;
import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.request.CastingUpsertRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.service.internal.CastingDataApplier;
import com.padimasso.autocasting.application.castings.service.CastingMediaCleanupService;
import com.padimasso.autocasting.application.castings.service.internal.CastingRoleDuplicator;
import com.padimasso.autocasting.application.castings.service.internal.CastingStatusTransitionPolicy;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingStatusRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingRowResponse;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingModalityOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProjectTypeOptionEntity;
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
import com.padimasso.autocasting.application.talent.service.MediaStorageService;
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

import static com.padimasso.autocasting.config.AppConstants.CASTING_APPLICATION_STATUS_BLANK;
import static com.padimasso.autocasting.config.AppConstants.CASTING_APPLICATION_STATUS_SELECTED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_ARCHIVED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_CLOSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PAUSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static com.padimasso.autocasting.config.AppConstants.CURRENCY_ARS;
import static com.padimasso.autocasting.config.AppConstants.PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID;
import static com.padimasso.autocasting.config.AppConstants.GENDER_OPTION_INDISTINCT;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_ADMIN_NOT_EDITABLE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_INVALID_STATUS_TRANSITION;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_LAST_ROLE_REQUIRED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_NOT_PUBLISHABLE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_ROLE_HAS_SELECTED_APPLICATIONS;
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
import static org.mockito.Mockito.never;
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
    private CastingApplicationRepository castingApplicationRepository;
    @Mock
    private MediaStorageService mediaStorageService;
    @Mock
    private CastingMediaCleanupService castingMediaCleanupService;
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
        var dataApplier = new CastingDataApplier(siteMetadataResolver);
        service = new AdminCastingEditServiceImpl(
            castingRepository, castingRoleRepository, castingApplicationRepository, dataApplier,
            new CastingRoleDuplicator(dataApplier), mediaStorageService, new CastingStatusTransitionPolicy(),
            siteMetadataResolver, castingMediaCleanupService, adminCastingMapper, historyService, objectMapper
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

    // ---- create / duplicate / delete

    private static ApplicationStatusCountProjection count(String statusCode, long total) {
        return new ApplicationStatusCountProjection() {
            @Override
            public String getStatusCode() {
                return statusCode;
            }

            @Override
            public long getTotal() {
                return total;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private List<HistoryChangeEntry> capturedChanges(EntityType type, UUID entityId, String reason) {
        ArgumentCaptor<Object> changes = ArgumentCaptor.forClass(Object.class);
        verify(historyService).createHistoryEntry(eq(type), eq(entityId), eq(reason), changes.capture());
        return (List<HistoryChangeEntry>) changes.getValue();
    }

    private final UUID newRoleId = UUID.randomUUID();

    private CastingRoleEntity persisted(CastingRoleEntity role) {
        role.setId(newRoleId);
        return role;
    }

    @Test
    void createCastingRole_savesRoleWithoutPhotoEvenIfClientSendsOne() {
        stubResolutions();
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(casting));
        when(castingRoleRepository.saveAndFlush(any())).thenAnswer(invocation -> persisted(invocation.getArgument(0)));
        var created = roleResponse("Villain");
        when(adminCastingMapper.toRoleResponse(any(CastingRoleEntity.class))).thenReturn(created);

        var result = service.createCastingRole(new AdminCastingRoleCreateRequest("new role", roleRequest(castingId, "Villain")));

        assertSame(created, result);
        ArgumentCaptor<CastingRoleEntity> saved = ArgumentCaptor.forClass(CastingRoleEntity.class);
        verify(castingRoleRepository).saveAndFlush(saved.capture());
        assertEquals("Villain", saved.getValue().getRoleName());
        assertSame(casting, saved.getValue().getCasting());
        assertEquals(null, saved.getValue().getReferencePhotoUrl());
        var entries = capturedChanges(EntityType.CASTING_ROLE, newRoleId, "new role");
        assertTrue(entries.stream().anyMatch(entry -> entry.fieldKey().equals("role.roleName") && "Villain".equals(entry.newValue())));
        assertTrue(entries.stream().noneMatch(entry -> entry.fieldKey().contains("referencePhotoUrl")));
    }

    @Test
    void createCastingRole_unknownOrClosedCasting_isRejected() {
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.empty());
        var missing = assertThrows(IllegalArgumentException.class,
            () -> service.createCastingRole(new AdminCastingRoleCreateRequest("r", roleRequest(castingId, "A"))));
        assertEquals(CASTINGS_NOT_FOUND, missing.getMessage());

        casting.setStatus(status(CASTING_STATUS_CLOSED));
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(casting));
        var closed = assertThrows(IllegalStateException.class,
            () -> service.createCastingRole(new AdminCastingRoleCreateRequest("r", roleRequest(castingId, "A"))));
        assertEquals(CASTINGS_ADMIN_NOT_EDITABLE, closed.getMessage());

        verify(castingRoleRepository, never()).saveAndFlush(any());
        verifyNoInteractions(historyService);
    }

    @Test
    void duplicateCastingRole_copiesWithoutPhotoAndRecordsTheSource() {
        var source = role(castingId);
        source.setAgeMin((short) 18);
        source.setAgeMax((short) 30);
        source.setPayRateType(payRate("sitemetadata.pay_rate.unpaid"));
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(source));
        when(castingRoleRepository.saveAndFlush(any())).thenAnswer(invocation -> persisted(invocation.getArgument(0)));
        when(adminCastingMapper.toRoleResponse(any(CastingRoleEntity.class))).thenReturn(roleResponse("Copia de Lead"));
        lenient().when(siteMetadataResolver.resolveCurrencyByCodeOrThrow(CURRENCY_ARS)).thenReturn(new CurrencyOptionEntity());

        service.duplicateCastingRole(roleId, new AdminCastingRoleDuplicateRequest("copy", "  Copia de Lead "));

        ArgumentCaptor<CastingRoleEntity> saved = ArgumentCaptor.forClass(CastingRoleEntity.class);
        verify(castingRoleRepository).saveAndFlush(saved.capture());
        assertEquals("Copia de Lead", saved.getValue().getRoleName());
        assertEquals(null, saved.getValue().getReferencePhotoUrl());
        assertSame(source.getCasting(), saved.getValue().getCasting());
        var entries = capturedChanges(EntityType.CASTING_ROLE, newRoleId, "copy");
        assertTrue(entries.stream().anyMatch(entry -> entry.fieldKey().equals("role.duplicatedFrom") && roleId.toString().equals(entry.newValue())));
    }

    @Test
    void duplicateCastingRole_roleOfClosedCasting_isRejected() {
        var source = role(castingId);
        source.getCasting().setStatus(status(CASTING_STATUS_ARCHIVED));
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(source));

        var error = assertThrows(IllegalStateException.class,
            () -> service.duplicateCastingRole(roleId, new AdminCastingRoleDuplicateRequest("r", null)));

        assertEquals(CASTINGS_ADMIN_NOT_EDITABLE, error.getMessage());
        verify(castingRoleRepository, never()).saveAndFlush(any());
    }

    private static PayRateTypeOptionEntity payRate(String code) {
        var payRate = new PayRateTypeOptionEntity();
        payRate.setStringCode(code);
        return payRate;
    }

    @Test
    void deleteCastingRole_withSelectedApplication_isRejectedAndKeepsEverything() {
        var entity = role(castingId);
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(entity));
        when(castingApplicationRepository.countByStatusForRole(roleId))
            .thenReturn(List.of(count(CASTING_APPLICATION_STATUS_BLANK, 3), count(CASTING_APPLICATION_STATUS_SELECTED, 1)));

        var error = assertThrows(IllegalStateException.class,
            () -> service.deleteCastingRole(roleId, new AdminCastingRoleDeleteRequest("r")));

        assertEquals(CASTINGS_ROLE_HAS_SELECTED_APPLICATIONS, error.getMessage());
        verify(castingRoleRepository, never()).softDelete(any());
        verifyNoInteractions(historyService, mediaStorageService);
    }

    @Test
    void deleteCastingRole_lastRoleOfPublishedOrPausedCasting_isRejected() {
        for (String code : List.of(CASTING_STATUS_PUBLISHED, CASTING_STATUS_PAUSED)) {
            var entity = role(castingId);
            entity.getCasting().setStatus(status(code));
            when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(entity));
            when(castingApplicationRepository.countByStatusForRole(roleId)).thenReturn(List.of());
            when(castingRoleRepository.countByCasting_IdAndDeletedFalse(castingId)).thenReturn(1L);

            var error = assertThrows(IllegalStateException.class,
                () -> service.deleteCastingRole(roleId, new AdminCastingRoleDeleteRequest("r")));

            assertEquals(CASTINGS_LAST_ROLE_REQUIRED, error.getMessage());
        }
        verify(castingRoleRepository, never()).softDelete(any());
        verifyNoInteractions(historyService);
    }

    @Test
    void deleteCastingRole_lastRoleOfDraftCasting_isAllowed() {
        var entity = role(castingId);
        entity.getCasting().setStatus(status(CASTING_STATUS_DRAFT));
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(entity));
        when(castingApplicationRepository.countByStatusForRole(roleId)).thenReturn(List.of());
        when(adminCastingMapper.toRoleResponse(entity)).thenReturn(roleResponse("Lead"));

        service.deleteCastingRole(roleId, new AdminCastingRoleDeleteRequest("cleanup"));

        verify(castingRoleRepository).softDelete(entity);
        verify(castingRoleRepository, never()).countByCasting_IdAndDeletedFalse(any());
    }

    @Test
    void deleteCastingRole_keepsApplicationsRemovesPhotoAndRecordsWhatTalentsKeep() {
        var entity = role(castingId);
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(entity));
        when(castingApplicationRepository.countByStatusForRole(roleId)).thenReturn(List.of(count(CASTING_APPLICATION_STATUS_BLANK, 2)));
        when(castingRoleRepository.countByCasting_IdAndDeletedFalse(castingId)).thenReturn(2L);
        when(adminCastingMapper.toRoleResponse(entity)).thenReturn(roleResponse("Lead"));

        service.deleteCastingRole(roleId, new AdminCastingRoleDeleteRequest("wrong role"));

        verify(castingRoleRepository).softDelete(entity);
        verify(mediaStorageService).deleteByPublicUrl("https://x/photo.png");
        var entries = capturedChanges(EntityType.CASTING_ROLE, roleId, "wrong role");
        assertTrue(entries.stream().anyMatch(entry -> entry.fieldKey().equals("role.deleted") && Boolean.TRUE.equals(entry.newValue())));
        var kept = entries.stream().filter(entry -> entry.fieldKey().equals("role.applicationsKept")).findFirst().orElseThrow();
        assertEquals(java.util.Map.of(CASTING_APPLICATION_STATUS_BLANK, 2L), kept.newValue());
    }

    // ---- status change

    private CastingEntity completeCasting(String statusCode) {
        var projectType = new ProjectTypeOptionEntity();
        projectType.setStringCode("sitemetadata.project_type.short_film");
        var modality = new CastingModalityOptionEntity();
        modality.setStringCode("sitemetadata.casting_modality.autocasting");
        var roleType = new RoleTypeOptionEntity();
        roleType.setStringCode("sitemetadata.role_type.protagonist");
        var gender = new GenderOptionEntity();
        gender.setStringCode(GENDER_OPTION_INDISTINCT);

        var complete = CastingEntity.builder()
            .id(castingId)
            .employerProfile(EmployerProfileEntity.builder().id(employerProfileId).build())
            .status(status(statusCode))
            .title("Film")
            .projectType(projectType)
            .castingModality(modality)
            .applicationDeadline(LocalDate.now().plusDays(30))
            .hasWardrobeFitting(false)
            .shootingStartDate(LocalDate.now().plusDays(40))
            .shootingEndDate(LocalDate.now().plusDays(41))
            .build();
        complete.getRoles().add(CastingRoleEntity.builder()
            .roleName("Lead").roleType(roleType).gender(gender)
            .ageMin((short) 18).ageMax((short) 30).payRateType(payRate("sitemetadata.pay_rate.unpaid"))
            .build());
        return complete;
    }

    private final UUID employerProfileId = UUID.randomUUID();

    private void stubStatusLookup(CastingEntity target, String newStatusCode) {
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(target));
        lenient().when(siteMetadataResolver.resolveCastingStatusByCodeOrThrow(newStatusCode)).thenReturn(status(newStatusCode));
        lenient().when(castingRepository.saveAndFlush(target)).thenReturn(target);
    }

    @Test
    void changeCastingStatus_publishedToPaused_appliesItAndRecordsTheChange() {
        var target = completeCasting(CASTING_STATUS_PUBLISHED);
        stubStatusLookup(target, CASTING_STATUS_PAUSED);
        var row = org.mockito.Mockito.mock(AdminCastingRowResponse.class);
        when(adminCastingMapper.toRowResponse(target)).thenReturn(row);

        var result = service.changeCastingStatus(castingId, new AdminCastingStatusRequest("on hold", CASTING_STATUS_PAUSED));

        assertSame(row, result);
        assertEquals(CASTING_STATUS_PAUSED, target.getStatus().getStringCode());
        var entries = capturedChanges(EntityType.CASTING, castingId, "on hold");
        assertEquals(1, entries.size());
        assertEquals("casting.status", entries.get(0).fieldKey());
        assertEquals(java.util.Map.of("stringCode", CASTING_STATUS_PUBLISHED), entries.get(0).previousValue());
        assertEquals(java.util.Map.of("stringCode", CASTING_STATUS_PAUSED), entries.get(0).newValue());
        verify(castingMediaCleanupService, never()).deleteCastingFolder(any(), any());
    }

    @Test
    void changeCastingStatus_closing_removesTheStorageFolder() {
        var target = completeCasting(CASTING_STATUS_PAUSED);
        stubStatusLookup(target, CASTING_STATUS_CLOSED);

        service.changeCastingStatus(castingId, new AdminCastingStatusRequest("done", CASTING_STATUS_CLOSED));

        assertEquals(CASTING_STATUS_CLOSED, target.getStatus().getStringCode());
        verify(castingMediaCleanupService).deleteCastingFolder(employerProfileId, castingId);
    }

    @Test
    void changeCastingStatus_pausedToPublished_isAllowedOnlyWhenTheCastingIsPublishable() {
        var complete = completeCasting(CASTING_STATUS_PAUSED);
        stubStatusLookup(complete, CASTING_STATUS_PUBLISHED);
        service.changeCastingStatus(castingId, new AdminCastingStatusRequest("back", CASTING_STATUS_PUBLISHED));
        assertEquals(CASTING_STATUS_PUBLISHED, complete.getStatus().getStringCode());

        var incomplete = completeCasting(CASTING_STATUS_PAUSED);
        incomplete.setTitle(null);
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(incomplete));
        var error = assertThrows(IllegalStateException.class,
            () -> service.changeCastingStatus(castingId, new AdminCastingStatusRequest("back", CASTING_STATUS_PUBLISHED)));
        assertEquals(CASTINGS_NOT_PUBLISHABLE, error.getMessage());
        assertEquals(CASTING_STATUS_PAUSED, incomplete.getStatus().getStringCode());
    }

    @Test
    void changeCastingStatus_neverPublishesADraftEvenWhenComplete() {
        var draft = completeCasting(CASTING_STATUS_DRAFT);
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(draft));

        var error = assertThrows(IllegalStateException.class,
            () -> service.changeCastingStatus(castingId, new AdminCastingStatusRequest("r", CASTING_STATUS_PUBLISHED)));

        assertEquals(CASTINGS_INVALID_STATUS_TRANSITION, error.getMessage());
        assertEquals(CASTING_STATUS_DRAFT, draft.getStatus().getStringCode());
        verifyNoInteractions(historyService);
    }

    @Test
    void changeCastingStatus_followsTheEmployerTransitions() {
        for (var forbidden : List.of(
            List.of(CASTING_STATUS_CLOSED, CASTING_STATUS_PAUSED),
            List.of(CASTING_STATUS_CLOSED, CASTING_STATUS_PUBLISHED),
            List.of(CASTING_STATUS_ARCHIVED, CASTING_STATUS_CLOSED),
            List.of(CASTING_STATUS_PUBLISHED, CASTING_STATUS_DRAFT),
            List.of(CASTING_STATUS_PUBLISHED, "sitemetadata.casting_status.unknown")
        )) {
            var target = completeCasting(forbidden.get(0));
            when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(target));

            var error = assertThrows(IllegalStateException.class,
                () -> service.changeCastingStatus(castingId, new AdminCastingStatusRequest("r", forbidden.get(1))));

            assertEquals(CASTINGS_INVALID_STATUS_TRANSITION, error.getMessage());
            assertEquals(forbidden.get(0), target.getStatus().getStringCode());
        }
        verify(castingRepository, never()).saveAndFlush(any());
        verifyNoInteractions(historyService, castingMediaCleanupService);
    }

    @Test
    void changeCastingStatus_afterTheDeadlineOnlyClosingAndArchivingAreOffered() {
        var expired = completeCasting(CASTING_STATUS_PUBLISHED);
        expired.setApplicationDeadline(LocalDate.now().minusDays(1));
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(expired));

        var error = assertThrows(IllegalStateException.class,
            () -> service.changeCastingStatus(castingId, new AdminCastingStatusRequest("r", CASTING_STATUS_PAUSED)));

        assertEquals(CASTINGS_INVALID_STATUS_TRANSITION, error.getMessage());
    }

    @Test
    void changeCastingStatus_proposalOrUnknownCasting_isNotFound() {
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.empty());
        assertEquals(CASTINGS_NOT_FOUND, assertThrows(IllegalArgumentException.class,
            () -> service.changeCastingStatus(castingId, new AdminCastingStatusRequest("r", CASTING_STATUS_PAUSED))).getMessage());

        var proposal = completeCasting(CASTING_STATUS_PUBLISHED);
        proposal.getEmployerProfile().setId(PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID);
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(proposal));
        assertEquals(CASTINGS_NOT_FOUND, assertThrows(IllegalArgumentException.class,
            () -> service.changeCastingStatus(castingId, new AdminCastingStatusRequest("r", CASTING_STATUS_PAUSED))).getMessage());
        verifyNoInteractions(historyService);
    }
}
