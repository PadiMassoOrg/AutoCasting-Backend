package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.applications.repository.CastingApplicationRepository;
import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.context.EmployerContext;
import com.padimasso.autocasting.application.auth.dto.response.EmployerPrincipal;
import com.padimasso.autocasting.application.castings.dto.response.EmployerCastingCheckoutSummaryResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.service.CastingMediaCleanupService;
import com.padimasso.autocasting.application.castings.service.internal.CastingStatusTransitionPolicy;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.sitemetadata.model.CastingModalityOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CurrencyOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.PayRateTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProjectTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.RoleTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CastingServiceImplCheckoutSummaryTest {

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
    private CastingMediaCleanupService castingMediaCleanupService;

    private CastingServiceImpl service;

    private UUID employerProfileId;
    private UUID castingId;

    @BeforeEach
    void setUp() {
        // Uses a real CastingMapper (a plain, dependency-free @Component) instead of a mock,
        // so these tests verify the actual response shape produced for the checkout screen —
        // not just that the mapper was called.
        service = new CastingServiceImpl(
            authContext,
            talentProfileRepository,
            employerContext,
            employerProfileRepository,
            castingRepository,
            siteMetadataResolver,
            castingStatusTransitionPolicy,
            castingApplicationRepository,
            new CastingMapper(),
            castingMediaCleanupService
        );

        employerProfileId = UUID.randomUUID();
        castingId = UUID.randomUUID();
        EmployerProfileEntity employerProfile = EmployerProfileEntity.builder().id(employerProfileId).build();
        EmployerPrincipal principal = new EmployerPrincipal(null, employerProfile);
        lenient().when(employerContext.getCurrentEmployerOrThrow()).thenReturn(principal);
    }

    @Test
    void getEmployerCastingCheckoutSummary_nullCastingId_throws() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.getEmployerCastingCheckoutSummary(null));

        assertEquals("casting.id_required", exception.getMessage());
    }

    @Test
    void getEmployerCastingCheckoutSummary_notFoundForEmployer_throws() {
        when(castingRepository.findByIdAndEmployerProfile_IdAndDeletedFalse(castingId, employerProfileId))
            .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.getEmployerCastingCheckoutSummary(castingId));

        assertEquals("castings.not_found", exception.getMessage());
    }

    @Test
    void getEmployerCastingCheckoutSummary_mapsBasicCastingFields() {
        ProjectTypeOptionEntity projectType = new ProjectTypeOptionEntity();
        projectType.setId(UUID.randomUUID());
        projectType.setStringCode("sitemetadata.project_type.film");

        CastingModalityOptionEntity modality = new CastingModalityOptionEntity();
        modality.setId(UUID.randomUUID());
        modality.setStringCode("sitemetadata.casting_modality.on_site");

        LocalDate deadline = LocalDate.now().plusDays(15);

        CastingEntity casting = CastingEntity.builder()
            .id(castingId)
            .defaultCode("C-ABCDEF12")
            .employerProfile(EmployerProfileEntity.builder().id(employerProfileId).build())
            .title("Feature Film Lead")
            .projectType(projectType)
            .castingModality(modality)
            .applicationDeadline(deadline)
            .roles(Set.of())
            .build();

        when(castingRepository.findByIdAndEmployerProfile_IdAndDeletedFalse(castingId, employerProfileId))
            .thenReturn(Optional.of(casting));

        EmployerCastingCheckoutSummaryResponse response = service.getEmployerCastingCheckoutSummary(castingId);

        assertEquals(castingId, response.id());
        assertEquals("C-ABCDEF12", response.defaultCode());
        assertEquals("Feature Film Lead", response.castingTitle());
        assertEquals(projectType.getId(), response.projectType().id());
        assertEquals("sitemetadata.project_type.film", response.projectType().stringCode());
        assertEquals(modality.getId(), response.castingModality().id());
        assertEquals(deadline, response.applicationDeadline());
        assertTrue(response.roles().isEmpty());
    }

    @Test
    void getEmployerCastingCheckoutSummary_nullProjectTypeAndModality_mapToNull() {
        CastingEntity casting = CastingEntity.builder()
            .id(castingId)
            .employerProfile(EmployerProfileEntity.builder().id(employerProfileId).build())
            .title("Untitled")
            .roles(Set.of())
            .build();

        when(castingRepository.findByIdAndEmployerProfile_IdAndDeletedFalse(castingId, employerProfileId))
            .thenReturn(Optional.of(casting));

        EmployerCastingCheckoutSummaryResponse response = service.getEmployerCastingCheckoutSummary(castingId);

        assertNull(response.projectType());
        assertNull(response.castingModality());
    }

    @Test
    void getEmployerCastingCheckoutSummary_mapsRoleWithAmountCurrencyAndPayRateType() {
        RoleTypeOptionEntity roleType = new RoleTypeOptionEntity();
        roleType.setId(UUID.randomUUID());
        roleType.setStringCode("sitemetadata.role_type.protagonist");

        PayRateTypeOptionEntity payRateType = new PayRateTypeOptionEntity();
        payRateType.setId(UUID.randomUUID());
        payRateType.setStringCode("sitemetadata.pay_rate_type.fixed");

        CurrencyOptionEntity currency = new CurrencyOptionEntity();
        currency.setId(UUID.randomUUID());
        currency.setStringCode("sitemetadata.currency.ars");

        UUID roleId = UUID.randomUUID();
        CastingRoleEntity role = CastingRoleEntity.builder()
            .id(roleId)
            .roleName("Lead")
            .roleType(roleType)
            .payRateType(payRateType)
            .currency(currency)
            .amount(new BigDecimal("1500.00"))
            .build();

        CastingEntity casting = CastingEntity.builder()
            .id(castingId)
            .employerProfile(EmployerProfileEntity.builder().id(employerProfileId).build())
            .title("Feature Film Lead")
            .roles(new HashSet<>(Set.of(role)))
            .build();

        when(castingRepository.findByIdAndEmployerProfile_IdAndDeletedFalse(castingId, employerProfileId))
            .thenReturn(Optional.of(casting));

        EmployerCastingCheckoutSummaryResponse response = service.getEmployerCastingCheckoutSummary(castingId);

        assertEquals(1, response.roles().size());
        var roleResponse = response.roles().get(0);
        assertEquals(roleId, roleResponse.id());
        assertEquals("Lead", roleResponse.roleName());
        assertEquals(roleType.getId(), roleResponse.roleType().id());
        assertEquals(payRateType.getId(), roleResponse.payRateType().id());
        assertEquals(currency.getId(), roleResponse.currency().id());
        assertEquals(new BigDecimal("1500.00"), roleResponse.amount());
    }

    @Test
    void getEmployerCastingCheckoutSummary_excludesDeletedRoles() {
        CastingRoleEntity activeRole = CastingRoleEntity.builder().id(UUID.randomUUID()).roleName("Active").build();
        CastingRoleEntity deletedRole = CastingRoleEntity.builder().id(UUID.randomUUID()).roleName("Deleted").build();
        deletedRole.setDeleted(true);

        CastingEntity casting = CastingEntity.builder()
            .id(castingId)
            .employerProfile(EmployerProfileEntity.builder().id(employerProfileId).build())
            .title("Feature Film Lead")
            .roles(new HashSet<>(Set.of(activeRole, deletedRole)))
            .build();

        when(castingRepository.findByIdAndEmployerProfile_IdAndDeletedFalse(castingId, employerProfileId))
            .thenReturn(Optional.of(casting));

        EmployerCastingCheckoutSummaryResponse response = service.getEmployerCastingCheckoutSummary(castingId);

        assertEquals(1, response.roles().size());
        assertEquals("Active", response.roles().get(0).roleName());
    }

    @Test
    void getEmployerCastingCheckoutSummary_unpaidRoleWithoutAmount_mapsNullAmountAndCurrency() {
        PayRateTypeOptionEntity unpaid = new PayRateTypeOptionEntity();
        unpaid.setId(UUID.randomUUID());
        unpaid.setStringCode("sitemetadata.pay_rate_type.unpaid");

        CastingRoleEntity role = CastingRoleEntity.builder()
            .id(UUID.randomUUID())
            .roleName("Extra")
            .payRateType(unpaid)
            .currency(null)
            .amount(null)
            .build();

        CastingEntity casting = CastingEntity.builder()
            .id(castingId)
            .employerProfile(EmployerProfileEntity.builder().id(employerProfileId).build())
            .title("Feature Film Lead")
            .roles(Set.of(role))
            .build();

        when(castingRepository.findByIdAndEmployerProfile_IdAndDeletedFalse(castingId, employerProfileId))
            .thenReturn(Optional.of(casting));

        EmployerCastingCheckoutSummaryResponse response = service.getEmployerCastingCheckoutSummary(castingId);

        var roleResponse = response.roles().get(0);
        assertEquals(unpaid.getId(), roleResponse.payRateType().id());
        assertNull(roleResponse.currency());
        assertNull(roleResponse.amount());
    }
}
