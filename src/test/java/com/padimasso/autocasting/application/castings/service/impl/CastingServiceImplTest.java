package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.applications.repository.CastingApplicationRepository;
import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.context.EmployerContext;
import com.padimasso.autocasting.application.auth.dto.response.EmployerPrincipal;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.service.CastingMediaCleanupService;
import com.padimasso.autocasting.application.castings.service.internal.CastingStatusTransitionPolicy;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.sitemetadata.model.CastingModalityOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CurrencyOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.GenderOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.PayRateTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProjectTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.RoleTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CastingServiceImplTest {

    @Mock
    private AuthContext authContext;
    @Mock
    private TalentProfileRepository talentProfileRepository;
    @Mock
    private EmployerContext employerContext;
    @Mock
    private EmployerProfileRepository employerProfileRepository;
    @Mock
    private CastingRepository castingRepository;
    @Mock
    private SiteMetadataResolver siteMetadataResolver;
    @Mock
    private CastingStatusTransitionPolicy castingStatusTransitionPolicy;
    @Mock
    private CastingApplicationRepository castingApplicationRepository;
    @Mock
    private CastingMapper castingMapper;
    @Mock
    private CastingMediaCleanupService castingMediaCleanupService;

    private CastingServiceImpl service;

    private UUID employerProfileId;
    private static final String SLUG = "C-12345678";

    @BeforeEach
    void setUp() {
        service = new CastingServiceImpl(
            authContext,
            talentProfileRepository,
            employerContext,
            employerProfileRepository,
            castingRepository,
            siteMetadataResolver,
            castingStatusTransitionPolicy,
            castingApplicationRepository,
            castingMapper,
            castingMediaCleanupService
        );

        employerProfileId = UUID.randomUUID();
        EmployerProfileEntity employerProfile = EmployerProfileEntity.builder().id(employerProfileId).build();
        EmployerPrincipal principal = new EmployerPrincipal(null, employerProfile);
        org.mockito.Mockito.lenient().when(employerContext.getCurrentEmployerOrThrow()).thenReturn(principal);
    }

    private CastingModalityOptionEntity modality(String code) {
        CastingModalityOptionEntity modality = new CastingModalityOptionEntity();
        modality.setStringCode(code);
        return modality;
    }

    private ProjectTypeOptionEntity anyProjectType() {
        return new ProjectTypeOptionEntity();
    }

    private CastingRoleEntity completeRole(PayRateTypeOptionEntity payRateType, CurrencyOptionEntity currency, BigDecimal amount) {
        RoleTypeOptionEntity roleType = new RoleTypeOptionEntity();
        GenderOptionEntity gender = new GenderOptionEntity();
        return CastingRoleEntity.builder()
            .id(UUID.randomUUID())
            .roleName("Lead")
            .roleType(roleType)
            .gender(gender)
            .ageMin((short) 18)
            .ageMax((short) 30)
            .payRateType(payRateType)
            .currency(currency)
            .amount(amount)
            .build();
    }

    private CastingEntity completeCastingBase() {
        return CastingEntity.builder()
            .id(UUID.randomUUID())
            .defaultCode(SLUG)
            .employerProfile(EmployerProfileEntity.builder().id(employerProfileId).build())
            .title("A title")
            .projectType(anyProjectType())
            .castingModality(modality(CASTING_MODALITY_AUTOCASTING))
            .applicationDeadline(LocalDate.now().plusDays(10))
            .hasWardrobeFitting(false)
            .shootingStartDate(LocalDate.now().plusDays(20))
            .shootingEndDate(LocalDate.now().plusDays(25))
            .build();
    }

    private boolean invokeIsPublishableViaEditor(CastingEntity casting) {
        when(castingRepository.findByDefaultCodeAndEmployerProfile_IdAndDeletedFalse(SLUG, employerProfileId))
            .thenReturn(Optional.of(casting));

        ArgumentCaptor<Boolean> publishableCaptor = ArgumentCaptor.forClass(Boolean.class);
        service.getCastingEditorBySlug(SLUG);

        org.mockito.Mockito.verify(castingMapper).toEmployerCastingEditorResponse(eq(casting), publishableCaptor.capture());
        return publishableCaptor.getValue();
    }

    // ---- hasCompleteBasicInfo / isPublishable: basic info gate ----

    @Test
    void isPublishable_missingTitle_notPublishable() {
        CastingEntity casting = completeCastingBase();
        casting.setTitle(null);
        casting.setRoles(Set.of(completeRole(unpaidPayRateType(), null, null)));

        assertFalse(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void isPublishable_missingProjectType_notPublishable() {
        CastingEntity casting = completeCastingBase();
        casting.setProjectType(null);
        casting.setRoles(Set.of(completeRole(unpaidPayRateType(), null, null)));

        assertFalse(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void isPublishable_missingDeadline_notPublishable() {
        CastingEntity casting = completeCastingBase();
        casting.setApplicationDeadline(null);
        casting.setRoles(Set.of(completeRole(unpaidPayRateType(), null, null)));

        assertFalse(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void isPublishable_missingShootingDates_notPublishable() {
        CastingEntity casting = completeCastingBase();
        casting.setShootingStartDate(null);
        casting.setRoles(Set.of(completeRole(unpaidPayRateType(), null, null)));

        assertFalse(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void isPublishable_onSiteModalityWithoutLocation_notPublishable() {
        CastingEntity casting = completeCastingBase();
        casting.setCastingModality(modality(CASTING_MODALITY_ON_SITE));
        casting.setLocationText(null);
        casting.setRoles(Set.of(completeRole(unpaidPayRateType(), null, null)));

        assertFalse(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void isPublishable_onSiteModalityWithLocation_basicInfoComplete() {
        CastingEntity casting = completeCastingBase();
        casting.setCastingModality(modality(CASTING_MODALITY_ON_SITE));
        casting.setLocationText("Buenos Aires");
        casting.setRoles(Set.of(completeRole(unpaidPayRateType(), null, null)));

        assertTrue(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void isPublishable_wardrobeFittingTrueWithoutText_notPublishable() {
        CastingEntity casting = completeCastingBase();
        casting.setHasWardrobeFitting(true);
        casting.setWardrobeFittingText(null);
        casting.setRoles(Set.of(completeRole(unpaidPayRateType(), null, null)));

        assertFalse(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void isPublishable_wardrobeFittingTrueWithText_basicInfoComplete() {
        CastingEntity casting = completeCastingBase();
        casting.setHasWardrobeFitting(true);
        casting.setWardrobeFittingText("Bring your own shoes");
        casting.setRoles(Set.of(completeRole(unpaidPayRateType(), null, null)));

        assertTrue(invokeIsPublishableViaEditor(casting));
    }

    // ---- "at least one complete role" gate ----

    @Test
    void isPublishable_noRoles_notPublishable() {
        CastingEntity casting = completeCastingBase();
        casting.setRoles(Set.of());

        assertFalse(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void isPublishable_onlyDeletedRoles_notPublishable() {
        CastingEntity casting = completeCastingBase();
        CastingRoleEntity deletedRole = completeRole(unpaidPayRateType(), null, null);
        deletedRole.setDeleted(true);
        casting.setRoles(new HashSet<>(Set.of(deletedRole)));

        assertFalse(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void isPublishable_oneIncompleteRoleAmongMany_notPublishable() {
        CastingEntity casting = completeCastingBase();
        CastingRoleEntity complete = completeRole(unpaidPayRateType(), null, null);
        CastingRoleEntity incomplete = completeRole(unpaidPayRateType(), null, null);
        incomplete.setRoleName(null);
        casting.setRoles(new HashSet<>(Set.of(complete, incomplete)));

        assertFalse(invokeIsPublishableViaEditor(casting));
    }

    // ---- hasCompleteRole ----

    @Test
    void hasCompleteRole_missingRoleType_notPublishable() {
        CastingEntity casting = completeCastingBase();
        CastingRoleEntity role = completeRole(unpaidPayRateType(), null, null);
        role.setRoleType(null);
        casting.setRoles(Set.of(role));

        assertFalse(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void hasCompleteRole_ageMinGreaterThanAgeMax_notPublishable() {
        CastingEntity casting = completeCastingBase();
        CastingRoleEntity role = completeRole(unpaidPayRateType(), null, null);
        role.setAgeMin((short) 40);
        role.setAgeMax((short) 20);
        casting.setRoles(Set.of(role));

        assertFalse(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void hasCompleteRole_missingPayRateType_notPublishable() {
        CastingEntity casting = completeCastingBase();
        CastingRoleEntity role = completeRole(null, null, null);
        casting.setRoles(Set.of(role));

        assertFalse(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void hasCompleteRole_unpaidLike_completeWithoutAmountOrCurrency() {
        CastingEntity casting = completeCastingBase();
        CastingRoleEntity role = completeRole(unpaidPayRateType(), null, null);
        casting.setRoles(Set.of(role));

        assertTrue(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void hasCompleteRole_collaborative_completeWithoutAmountOrCurrency() {
        CastingEntity casting = completeCastingBase();
        PayRateTypeOptionEntity collaborative = new PayRateTypeOptionEntity();
        collaborative.setStringCode("sitemetadata.pay_rate_type.collaborative");
        CastingRoleEntity role = completeRole(collaborative, null, null);
        casting.setRoles(Set.of(role));

        assertTrue(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void hasCompleteRole_toBeAgreed_completeWithoutAmountOrCurrency() {
        CastingEntity casting = completeCastingBase();
        PayRateTypeOptionEntity toBeAgreed = new PayRateTypeOptionEntity();
        toBeAgreed.setStringCode("sitemetadata.pay_rate_type.to_be_agreed");
        CastingRoleEntity role = completeRole(toBeAgreed, null, null);
        casting.setRoles(Set.of(role));

        assertTrue(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void hasCompleteRole_paidWithoutCurrency_notPublishable() {
        CastingEntity casting = completeCastingBase();
        PayRateTypeOptionEntity paid = new PayRateTypeOptionEntity();
        paid.setStringCode("sitemetadata.pay_rate_type.fixed");
        CastingRoleEntity role = completeRole(paid, null, new BigDecimal("500.00"));
        casting.setRoles(Set.of(role));

        assertFalse(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void hasCompleteRole_paidWithZeroAmount_notPublishable() {
        CastingEntity casting = completeCastingBase();
        PayRateTypeOptionEntity paid = new PayRateTypeOptionEntity();
        paid.setStringCode("sitemetadata.pay_rate_type.fixed");
        CurrencyOptionEntity ars = new CurrencyOptionEntity();
        CastingRoleEntity role = completeRole(paid, ars, BigDecimal.ZERO);
        casting.setRoles(Set.of(role));

        assertFalse(invokeIsPublishableViaEditor(casting));
    }

    @Test
    void hasCompleteRole_paidWithAmountAndCurrency_publishable() {
        CastingEntity casting = completeCastingBase();
        PayRateTypeOptionEntity paid = new PayRateTypeOptionEntity();
        paid.setStringCode("sitemetadata.pay_rate_type.fixed");
        CurrencyOptionEntity ars = new CurrencyOptionEntity();
        CastingRoleEntity role = completeRole(paid, ars, new BigDecimal("500.00"));
        casting.setRoles(Set.of(role));

        assertTrue(invokeIsPublishableViaEditor(casting));
    }

    private PayRateTypeOptionEntity unpaidPayRateType() {
        PayRateTypeOptionEntity unpaid = new PayRateTypeOptionEntity();
        unpaid.setStringCode(PAY_RATE_TYPE_UNPAID);
        return unpaid;
    }

    // ---- deleteCasting ----

    @Test
    void deleteCasting_softDeletesAndCleansUpSupabaseFolder() {
        UUID castingId = UUID.randomUUID();
        EmployerProfileEntity employerProfile = EmployerProfileEntity.builder().id(employerProfileId).build();
        CastingEntity casting = CastingEntity.builder().id(castingId).employerProfile(employerProfile).build();
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(java.util.Optional.of(casting));

        service.deleteCasting(castingId);

        org.mockito.Mockito.verify(castingRepository).softDelete(casting);
        org.mockito.Mockito.verify(castingMediaCleanupService).deleteCastingFolder(employerProfileId, castingId);
    }

    @Test
    void deleteCasting_missingEmployerProfile_softDeletesWithoutCleanupCall() {
        UUID castingId = UUID.randomUUID();
        CastingEntity casting = CastingEntity.builder().id(castingId).employerProfile(null).build();
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(java.util.Optional.of(casting));

        service.deleteCasting(castingId);

        org.mockito.Mockito.verify(castingRepository).softDelete(casting);
        org.mockito.Mockito.verify(castingMediaCleanupService, org.mockito.Mockito.never())
            .deleteCastingFolder(any(), any());
    }

    @Test
    void deleteCasting_notFound_throws() {
        UUID castingId = UUID.randomUUID();
        when(castingRepository.findByIdAndDeletedFalse(castingId)).thenReturn(java.util.Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteCasting(castingId));

        org.mockito.Mockito.verify(castingMediaCleanupService, org.mockito.Mockito.never())
            .deleteCastingFolder(any(), any());
    }
}
