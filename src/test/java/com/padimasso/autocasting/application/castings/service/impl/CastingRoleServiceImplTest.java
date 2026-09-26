package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.service.internal.CastingDataApplier;
import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.*;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CastingRoleServiceImplTest {

    @Mock
    private CastingRoleRepository castingRoleRepository;
    @Mock
    private CastingRepository castingRepository;
    @Mock
    private SiteMetadataResolver siteMetadataResolver;
    @Mock
    private CastingMapper castingMapper;
    @Mock
    private MediaStorageService mediaStorageService;

    private CastingRoleServiceImpl service;

    private UUID castingId;
    private CastingEntity draftCasting;
    private RoleTypeOptionEntity roleType;
    private GenderOptionEntity indistinctGender;
    private CurrencyOptionEntity arsCurrency;

    @BeforeEach
    void setUp() {
        service = new CastingRoleServiceImpl(
            castingRoleRepository, castingRepository, castingMapper, mediaStorageService,
            new CastingDataApplier(siteMetadataResolver)
        );

        castingId = UUID.randomUUID();

        CastingStatusOptionEntity draftStatus = new CastingStatusOptionEntity();
        draftStatus.setStringCode(CASTING_STATUS_DRAFT);

        draftCasting = CastingEntity.builder()
            .id(castingId)
            .status(draftStatus)
            .build();

        roleType = new RoleTypeOptionEntity();
        roleType.setStringCode("sitemetadata.role_type.protagonist");

        indistinctGender = new GenderOptionEntity();
        indistinctGender.setStringCode(GENDER_OPTION_INDISTINCT);

        arsCurrency = new CurrencyOptionEntity();
        arsCurrency.setStringCode(CURRENCY_ARS);
    }

    private CastingRoleRequest baseRequestBuilder(UUID payRateTypeId, BigDecimal amount, UUID currencyId) {
        return new CastingRoleRequest(
            castingId,
            "Lead role",
            UUID.randomUUID(),
            null,
            (short) 18,
            (short) 30,
            "description",
            Set.of(),
            Set.of(),
            payRateTypeId,
            currencyId,
            amount,
            null,
            false,
            false,
            null,
            null,
            null,
            null,
            null,
            null
        );
    }

    private void stubCommonResolutions(PayRateTypeOptionEntity payRateType) {
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(draftCasting));
        when(siteMetadataResolver.resolveRoleTypeOrThrow(any())).thenReturn(roleType);
        when(siteMetadataResolver.resolveGenderByCodeOrThrow(GENDER_OPTION_INDISTINCT)).thenReturn(indistinctGender);
        when(siteMetadataResolver.resolveProfessionsOrThrow(any())).thenReturn(Set.of());
        when(siteMetadataResolver.resolveSkillsOrThrow(any())).thenReturn(Set.of());
        if (payRateType != null) {
            when(siteMetadataResolver.resolvePayRateTypeOrThrow(any())).thenReturn(payRateType);
        }
        org.mockito.Mockito.lenient().when(castingRoleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    // ---- age range validation ----

    @Test
    void createCastingRole_ageMinGreaterThanAgeMax_throws() {
        PayRateTypeOptionEntity unpaid = new PayRateTypeOptionEntity();
        unpaid.setStringCode(PAY_RATE_TYPE_UNPAID);
        stubCommonResolutions(unpaid);

        CastingRoleRequest invalidRequest = new CastingRoleRequest(
            castingId, "Lead role", UUID.randomUUID(), null, (short) 40, (short) 20,
            null, Set.of(), Set.of(), UUID.randomUUID(), null, null, null, false, false,
            null, null, null, null, null, null
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.createCastingRole(invalidRequest));

        assertEquals(CASTING_AGE_RANGE_INVALID, exception.getMessage());
    }

    @Test
    void createCastingRole_ageMinEqualsAgeMax_doesNotThrow() {
        PayRateTypeOptionEntity unpaid = new PayRateTypeOptionEntity();
        unpaid.setStringCode(PAY_RATE_TYPE_UNPAID);
        stubCommonResolutions(unpaid);
        when(castingMapper.toRoleResponse(any())).thenReturn(null);

        CastingRoleRequest request = new CastingRoleRequest(
            castingId, "Lead role", UUID.randomUUID(), null, (short) 25, (short) 25,
            null, Set.of(), Set.of(), UUID.randomUUID(), null, null, null, false, false,
            null, null, null, null, null, null
        );

        service.createCastingRole(request);
    }

    // ---- amount validation ----

    @Test
    void createCastingRole_negativeAmount_throws() {
        PayRateTypeOptionEntity paid = new PayRateTypeOptionEntity();
        paid.setStringCode("sitemetadata.pay_rate_type.fixed");
        stubCommonResolutions(paid);

        CastingRoleRequest request = baseRequestBuilder(UUID.randomUUID(), new BigDecimal("-10.00"), UUID.randomUUID());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.createCastingRole(request));

        assertEquals(CASTING_AMOUNT_NEGATIVE, exception.getMessage());
    }

    @Test
    void createCastingRole_paidRoleWithoutAmount_throwsAmountRequired() {
        PayRateTypeOptionEntity paid = new PayRateTypeOptionEntity();
        paid.setStringCode("sitemetadata.pay_rate_type.fixed");
        stubCommonResolutions(paid);

        CastingRoleRequest request = baseRequestBuilder(UUID.randomUUID(), null, null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.createCastingRole(request));

        assertEquals(CASTING_AMOUNT_REQUIRED, exception.getMessage());
    }

    // ---- pay-rate branching ----

    @Test
    void createCastingRole_unpaid_forcesAmountNullAndDefaultsCurrencyToArsWhenUnset() {
        PayRateTypeOptionEntity unpaid = new PayRateTypeOptionEntity();
        unpaid.setStringCode(PAY_RATE_TYPE_UNPAID);
        stubCommonResolutions(unpaid);
        when(siteMetadataResolver.resolveCurrencyByCodeOrThrow(CURRENCY_ARS)).thenReturn(arsCurrency);

        ArgumentCaptor<CastingRoleEntity> captor = ArgumentCaptor.forClass(CastingRoleEntity.class);
        when(castingRoleRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(castingMapper.toRoleResponse(any())).thenReturn(null);

        CastingRoleRequest request = baseRequestBuilder(UUID.randomUUID(), new BigDecimal("500.00"), null);

        service.createCastingRole(request);

        assertNull(captor.getValue().getAmount());
        assertEquals(arsCurrency, captor.getValue().getCurrency());
    }

    @Test
    void createCastingRole_toBeAgreed_forcesAmountNullAndDefaultsCurrencyToArsWhenUnset() {
        PayRateTypeOptionEntity toBeAgreed = new PayRateTypeOptionEntity();
        toBeAgreed.setStringCode(PAY_RATE_TYPE_TO_BE_AGREED);
        stubCommonResolutions(toBeAgreed);
        when(siteMetadataResolver.resolveCurrencyByCodeOrThrow(CURRENCY_ARS)).thenReturn(arsCurrency);

        ArgumentCaptor<CastingRoleEntity> captor = ArgumentCaptor.forClass(CastingRoleEntity.class);
        when(castingRoleRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(castingMapper.toRoleResponse(any())).thenReturn(null);

        CastingRoleRequest request = baseRequestBuilder(UUID.randomUUID(), new BigDecimal("500.00"), null);

        service.createCastingRole(request);

        assertNull(captor.getValue().getAmount());
        assertEquals(arsCurrency, captor.getValue().getCurrency());
    }

    @Test
    void createCastingRole_collaborative_defaultsCurrencyToArsWhenUnset() {
        PayRateTypeOptionEntity collaborative = new PayRateTypeOptionEntity();
        collaborative.setStringCode("sitemetadata.pay_rate_type.collaborative");
        stubCommonResolutions(collaborative);
        when(siteMetadataResolver.resolveCurrencyByCodeOrThrow(CURRENCY_ARS)).thenReturn(arsCurrency);

        ArgumentCaptor<CastingRoleEntity> captor = ArgumentCaptor.forClass(CastingRoleEntity.class);
        when(castingRoleRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(castingMapper.toRoleResponse(any())).thenReturn(null);

        CastingRoleRequest request = baseRequestBuilder(UUID.randomUUID(), null, null);

        service.createCastingRole(request);

        assertNull(captor.getValue().getAmount());
        assertEquals(arsCurrency, captor.getValue().getCurrency());
    }

    @Test
    void createCastingRole_cooperative_defaultsCurrencyToArsWhenUnset() {
        PayRateTypeOptionEntity cooperative = new PayRateTypeOptionEntity();
        cooperative.setStringCode("sitemetadata.pay_rate_type.cooperative");
        stubCommonResolutions(cooperative);
        when(siteMetadataResolver.resolveCurrencyByCodeOrThrow(CURRENCY_ARS)).thenReturn(arsCurrency);

        ArgumentCaptor<CastingRoleEntity> captor = ArgumentCaptor.forClass(CastingRoleEntity.class);
        when(castingRoleRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(castingMapper.toRoleResponse(any())).thenReturn(null);

        CastingRoleRequest request = baseRequestBuilder(UUID.randomUUID(), null, null);

        service.createCastingRole(request);

        assertNull(captor.getValue().getAmount());
        assertEquals(arsCurrency, captor.getValue().getCurrency());
    }

    @Test
    void createCastingRole_otherPayRate_defaultsCurrencyToArsWhenUnset() {
        PayRateTypeOptionEntity paid = new PayRateTypeOptionEntity();
        paid.setStringCode("sitemetadata.pay_rate_type.fixed");
        stubCommonResolutions(paid);
        when(siteMetadataResolver.resolveCurrencyByCodeOrThrow(CURRENCY_ARS)).thenReturn(arsCurrency);

        ArgumentCaptor<CastingRoleEntity> captor = ArgumentCaptor.forClass(CastingRoleEntity.class);
        when(castingRoleRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(castingMapper.toRoleResponse(any())).thenReturn(null);

        CastingRoleRequest request = baseRequestBuilder(UUID.randomUUID(), new BigDecimal("1000.00"), null);

        service.createCastingRole(request);

        assertEquals(new BigDecimal("1000.00"), captor.getValue().getAmount());
        assertEquals(arsCurrency, captor.getValue().getCurrency());
    }

    @Test
    void createCastingRole_otherPayRate_keepsExplicitCurrency() {
        PayRateTypeOptionEntity paid = new PayRateTypeOptionEntity();
        paid.setStringCode("sitemetadata.pay_rate_type.fixed");
        UUID currencyId = UUID.randomUUID();
        CurrencyOptionEntity usd = new CurrencyOptionEntity();
        usd.setStringCode("sitemetadata.currency.usd");

        stubCommonResolutions(paid);
        when(siteMetadataResolver.resolveCurrencyOrThrow(currencyId)).thenReturn(usd);

        ArgumentCaptor<CastingRoleEntity> captor = ArgumentCaptor.forClass(CastingRoleEntity.class);
        when(castingRoleRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(castingMapper.toRoleResponse(any())).thenReturn(null);

        CastingRoleRequest request = baseRequestBuilder(UUID.randomUUID(), new BigDecimal("1000.00"), currencyId);

        service.createCastingRole(request);

        assertEquals(usd, captor.getValue().getCurrency());
    }

    // ---- reference photo url ----

    @Test
    void createCastingRole_setsTrimmedReferencePhotoUrl() {
        PayRateTypeOptionEntity unpaid = new PayRateTypeOptionEntity();
        unpaid.setStringCode(PAY_RATE_TYPE_UNPAID);
        stubCommonResolutions(unpaid);

        ArgumentCaptor<CastingRoleEntity> captor = ArgumentCaptor.forClass(CastingRoleEntity.class);
        when(castingRoleRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(castingMapper.toRoleResponse(any())).thenReturn(null);

        CastingRoleRequest request = new CastingRoleRequest(
            castingId, "Lead role", UUID.randomUUID(), null, (short) 18, (short) 30,
            null, Set.of(), Set.of(), UUID.randomUUID(), null, null, null, false, false,
            null, null, null, null, null, "  https://example.com/photo.jpg  "
        );

        service.createCastingRole(request);

        assertEquals("https://example.com/photo.jpg", captor.getValue().getReferencePhotoUrl());
    }

    @Test
    void createCastingRole_blankReferencePhotoUrl_normalizedToNull() {
        PayRateTypeOptionEntity unpaid = new PayRateTypeOptionEntity();
        unpaid.setStringCode(PAY_RATE_TYPE_UNPAID);
        stubCommonResolutions(unpaid);

        ArgumentCaptor<CastingRoleEntity> captor = ArgumentCaptor.forClass(CastingRoleEntity.class);
        when(castingRoleRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(castingMapper.toRoleResponse(any())).thenReturn(null);

        CastingRoleRequest request = new CastingRoleRequest(
            castingId, "Lead role", UUID.randomUUID(), null, (short) 18, (short) 30,
            null, Set.of(), Set.of(), UUID.randomUUID(), null, null, null, false, false,
            null, null, null, null, null, "   "
        );

        service.createCastingRole(request);

        assertNull(captor.getValue().getReferencePhotoUrl());
    }

    @Test
    void updateCastingRole_updatesReferencePhotoUrl() {
        PayRateTypeOptionEntity unpaid = new PayRateTypeOptionEntity();
        unpaid.setStringCode(PAY_RATE_TYPE_UNPAID);

        UUID roleId = UUID.randomUUID();
        CastingRoleEntity existingRole = CastingRoleEntity.builder()
            .id(roleId)
            .casting(draftCasting)
            .referencePhotoUrl("https://example.com/old.jpg")
            .build();
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(existingRole));
        when(siteMetadataResolver.resolveRoleTypeOrThrow(any())).thenReturn(roleType);
        when(siteMetadataResolver.resolveGenderByCodeOrThrow(GENDER_OPTION_INDISTINCT)).thenReturn(indistinctGender);
        when(siteMetadataResolver.resolveProfessionsOrThrow(any())).thenReturn(Set.of());
        when(siteMetadataResolver.resolveSkillsOrThrow(any())).thenReturn(Set.of());
        when(siteMetadataResolver.resolvePayRateTypeOrThrow(any())).thenReturn(unpaid);
        when(castingRoleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(castingMapper.toRoleResponse(any())).thenReturn(null);

        CastingRoleRequest request = new CastingRoleRequest(
            castingId, "Lead role", UUID.randomUUID(), null, (short) 18, (short) 30,
            null, Set.of(), Set.of(), UUID.randomUUID(), null, null, null, false, false,
            null, null, null, null, null, "https://example.com/new.jpg"
        );

        service.updateCastingRole(roleId, request);

        assertEquals("https://example.com/new.jpg", existingRole.getReferencePhotoUrl());
    }

    @Test
    void updateCastingRole_clearsReferencePhotoUrl() {
        PayRateTypeOptionEntity unpaid = new PayRateTypeOptionEntity();
        unpaid.setStringCode(PAY_RATE_TYPE_UNPAID);

        UUID roleId = UUID.randomUUID();
        CastingRoleEntity existingRole = CastingRoleEntity.builder()
            .id(roleId)
            .casting(draftCasting)
            .referencePhotoUrl("https://example.com/old.jpg")
            .build();
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(existingRole));
        when(siteMetadataResolver.resolveRoleTypeOrThrow(any())).thenReturn(roleType);
        when(siteMetadataResolver.resolveGenderByCodeOrThrow(GENDER_OPTION_INDISTINCT)).thenReturn(indistinctGender);
        when(siteMetadataResolver.resolveProfessionsOrThrow(any())).thenReturn(Set.of());
        when(siteMetadataResolver.resolveSkillsOrThrow(any())).thenReturn(Set.of());
        when(siteMetadataResolver.resolvePayRateTypeOrThrow(any())).thenReturn(unpaid);
        when(castingRoleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(castingMapper.toRoleResponse(any())).thenReturn(null);

        CastingRoleRequest request = new CastingRoleRequest(
            castingId, "Lead role", UUID.randomUUID(), null, (short) 18, (short) 30,
            null, Set.of(), Set.of(), UUID.randomUUID(), null, null, null, false, false,
            null, null, null, null, null, null
        );

        service.updateCastingRole(roleId, request);

        assertNull(existingRole.getReferencePhotoUrl());
    }

    @Test
    void duplicateCastingRole_doesNotCopyReferencePhotoUrl() {
        // referencePhotoUrl must never be copied to a duplicate: it points at a single Supabase
        // object, and deleting/replacing it from either role would delete it out from under the
        // other (two roles silently sharing one file). The duplicate starts with no photo.
        PayRateTypeOptionEntity unpaid = new PayRateTypeOptionEntity();
        unpaid.setStringCode(PAY_RATE_TYPE_UNPAID);

        UUID roleId = UUID.randomUUID();
        CastingRoleEntity sourceRole = CastingRoleEntity.builder()
            .id(roleId)
            .casting(draftCasting)
            .roleName("Lead role")
            .payRateType(unpaid)
            .referencePhotoUrl("https://example.com/photo.jpg")
            .build();
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(sourceRole));

        ArgumentCaptor<CastingRoleEntity> captor = ArgumentCaptor.forClass(CastingRoleEntity.class);
        when(castingRoleRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(castingMapper.toRoleResponse(any())).thenReturn(null);

        service.duplicateCastingRole(roleId, "Copy");

        assertNull(captor.getValue().getReferencePhotoUrl());
    }

    @Test
    void deleteCastingRole_deletesReferencePhotoFromStorage() {
        UUID roleId = UUID.randomUUID();
        CastingRoleEntity role = CastingRoleEntity.builder()
            .id(roleId)
            .casting(draftCasting)
            .referencePhotoUrl("https://example.com/photo.jpg")
            .build();
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(role));

        service.deleteCastingRole(roleId);

        org.mockito.Mockito.verify(mediaStorageService).deleteByPublicUrl("https://example.com/photo.jpg");
    }

    @Test
    void deleteCastingRole_noReferencePhoto_stillCallsDeleteByPublicUrl() {
        // deleteByPublicUrl is documented as a no-op for null/blank input, so the service calls
        // it unconditionally rather than special-casing "no photo" here.
        UUID roleId = UUID.randomUUID();
        CastingRoleEntity role = CastingRoleEntity.builder().id(roleId).casting(draftCasting).build();
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(role));

        service.deleteCastingRole(roleId);

        org.mockito.Mockito.verify(mediaStorageService).deleteByPublicUrl(null);
    }

    // ---- assertDraftEditable guard ----

    @Test
    void createCastingRole_nonDraftCasting_throws() {
        CastingStatusOptionEntity publishedStatus = new CastingStatusOptionEntity();
        publishedStatus.setStringCode(CASTING_STATUS_PUBLISHED);
        CastingEntity publishedCasting = CastingEntity.builder().id(castingId).status(publishedStatus).build();

        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(Optional.of(publishedCasting));

        CastingRoleRequest request = baseRequestBuilder(UUID.randomUUID(), null, null);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> service.createCastingRole(request));

        assertEquals(CASTINGS_ONLY_DRAFT_EDITABLE, exception.getMessage());
    }

    @Test
    void updateCastingRole_nonDraftCasting_throws() {
        CastingStatusOptionEntity publishedStatus = new CastingStatusOptionEntity();
        publishedStatus.setStringCode(CASTING_STATUS_PUBLISHED);
        CastingEntity publishedCasting = CastingEntity.builder().id(castingId).status(publishedStatus).build();

        UUID roleId = UUID.randomUUID();
        CastingRoleEntity existingRole = CastingRoleEntity.builder().id(roleId).casting(publishedCasting).build();
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(existingRole));

        CastingRoleRequest request = baseRequestBuilder(UUID.randomUUID(), null, null);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> service.updateCastingRole(roleId, request));

        assertEquals(CASTINGS_ONLY_DRAFT_EDITABLE, exception.getMessage());
    }

    @Test
    void deleteCastingRole_nonDraftCasting_throws() {
        CastingStatusOptionEntity publishedStatus = new CastingStatusOptionEntity();
        publishedStatus.setStringCode(CASTING_STATUS_PUBLISHED);
        CastingEntity publishedCasting = CastingEntity.builder().id(castingId).status(publishedStatus).build();

        UUID roleId = UUID.randomUUID();
        CastingRoleEntity existingRole = CastingRoleEntity.builder().id(roleId).casting(publishedCasting).build();
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(existingRole));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> service.deleteCastingRole(roleId));

        assertEquals(CASTINGS_ONLY_DRAFT_EDITABLE, exception.getMessage());
    }

    @Test
    void duplicateCastingRole_nonDraftCasting_throws() {
        CastingStatusOptionEntity publishedStatus = new CastingStatusOptionEntity();
        publishedStatus.setStringCode(CASTING_STATUS_PUBLISHED);
        CastingEntity publishedCasting = CastingEntity.builder().id(castingId).status(publishedStatus).build();

        UUID roleId = UUID.randomUUID();
        CastingRoleEntity existingRole = CastingRoleEntity.builder().id(roleId).casting(publishedCasting).build();
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(existingRole));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> service.duplicateCastingRole(roleId, "Copy"));

        assertEquals(CASTINGS_ONLY_DRAFT_EDITABLE, exception.getMessage());
    }

    @Test
    void updateCastingRole_castingIdMismatch_throws() {
        UUID roleId = UUID.randomUUID();
        CastingRoleEntity existingRole = CastingRoleEntity.builder().id(roleId).casting(draftCasting).build();
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(existingRole));

        CastingRoleRequest request = new CastingRoleRequest(
            UUID.randomUUID(), "Lead role", UUID.randomUUID(), null, (short) 18, (short) 30,
            null, Set.of(), Set.of(), null, null, null, null, false, false,
            null, null, null, null, null, null
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.updateCastingRole(roleId, request));

        assertEquals(CASTINGS_ROLE_MISMATCH, exception.getMessage());
    }
}
