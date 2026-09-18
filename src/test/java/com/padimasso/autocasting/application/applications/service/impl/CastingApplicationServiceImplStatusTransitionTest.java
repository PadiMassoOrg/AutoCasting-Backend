package com.padimasso.autocasting.application.applications.service.impl;

import com.padimasso.autocasting.application.applications.dto.request.BulkCastingApplicationStatusRequest;
import com.padimasso.autocasting.application.applications.repository.CastingApplicationRepository;
import com.padimasso.autocasting.application.applications.repository.CastingApplicationRequirementSubmissionRepository;
import com.padimasso.autocasting.application.applications.mapper.CastingApplicationMapper;
import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.context.EmployerContext;
import com.padimasso.autocasting.application.auth.dto.response.EmployerPrincipal;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingApplicationStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.padimasso.autocasting.config.AppConstants.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * Applicant status changes are intentionally unconstrained (decided under AI-34, documented in
 * Confluence "Applicant Status Changes"): unlike casting status, there is no transition policy —
 * any status can move to any other status, individually or in bulk. These tests lock that
 * decision in place so a policy isn't accidentally reintroduced later without a corresponding
 * product decision and documentation update.
 */
@ExtendWith(MockitoExtension.class)
class CastingApplicationServiceImplStatusTransitionTest {

    @Mock
    private AuthContext authContext;
    @Mock
    private EmployerContext employerContext;
    @Mock
    private TalentProfileRepository talentProfileRepository;
    @Mock
    private CastingApplicationRepository castingApplicationRepository;
    @Mock
    private CastingApplicationRequirementSubmissionRepository castingApplicationRequirementSubmissionRepository;
    @Mock
    private CastingRoleRepository castingRoleRepository;
    @Mock
    private SiteMetadataResolver siteMetadataResolver;
    @Mock
    private CastingApplicationMapper castingApplicationMapper;

    private CastingApplicationServiceImpl service;

    private UUID employerProfileId;
    private UUID applicationId;

    private static final List<String> ALL_STATUSES = List.of(
        CASTING_APPLICATION_STATUS_BLANK,
        CASTING_APPLICATION_STATUS_VIEWED,
        CASTING_APPLICATION_STATUS_PRESELECTED,
        CASTING_APPLICATION_STATUS_SELECTED,
        CASTING_APPLICATION_STATUS_NOT_PROCEEDING
    );

    @BeforeEach
    void setUp() {
        service = new CastingApplicationServiceImpl(
            authContext,
            employerContext,
            talentProfileRepository,
            castingApplicationRepository,
            castingApplicationRequirementSubmissionRepository,
            castingRoleRepository,
            siteMetadataResolver,
            castingApplicationMapper
        );

        employerProfileId = UUID.randomUUID();
        applicationId = UUID.randomUUID();
        EmployerProfileEntity employerProfile = EmployerProfileEntity.builder().id(employerProfileId).build();
        EmployerPrincipal principal = new EmployerPrincipal(null, employerProfile);
        lenient().when(employerContext.getCurrentEmployerOrThrow()).thenReturn(principal);
    }

    private CastingApplicationStatusOptionEntity statusEntity(String code) {
        CastingApplicationStatusOptionEntity entity = new CastingApplicationStatusOptionEntity();
        entity.setStringCode(code);
        return entity;
    }

    // ---- each of the 5 named status actions succeeds unconditionally: setEmployerOwnedApplicationStatus
    // never reads or checks the application's current status before writing the new one, so there
    // is no "from" state to parametrize over — this absence is the behavior under test. ----

    @ParameterizedTest
    @MethodSource("allStatuses")
    void everyIndividualStatusAction_succeedsWithNoTransitionCheck(String targetStatus) {
        when(siteMetadataResolver.resolveCastingApplicationStatusByCodeOrThrow(targetStatus))
            .thenReturn(statusEntity(targetStatus));
        when(castingApplicationRepository.setStatusIfOwned(eq(applicationId), eq(employerProfileId), any()))
            .thenReturn(1);

        assertDoesNotThrow(() -> invokeStatusAction(targetStatus, applicationId));
    }

    private static Stream<String> allStatuses() {
        return ALL_STATUSES.stream();
    }

    private void invokeStatusAction(String statusCode, UUID appId) {
        if (CASTING_APPLICATION_STATUS_PRESELECTED.equals(statusCode)) {
            service.preselectCastingApplication(appId);
        } else if (CASTING_APPLICATION_STATUS_SELECTED.equals(statusCode)) {
            service.selectCastingApplication(appId);
        } else if (CASTING_APPLICATION_STATUS_VIEWED.equals(statusCode)) {
            service.viewCastingApplication(appId);
        } else if (CASTING_APPLICATION_STATUS_NOT_PROCEEDING.equals(statusCode)) {
            service.notProceedingCastingApplication(appId);
        } else if (CASTING_APPLICATION_STATUS_BLANK.equals(statusCode)) {
            service.blankCastingApplication(appId);
        } else {
            throw new IllegalStateException("Unmapped status in test: " + statusCode);
        }
    }

    // ---- "reverse" moves that a transition policy would typically forbid are explicitly allowed ----

    @Test
    void notProceeding_thenBackToPreselected_bothIndividualCallsSucceed() {
        when(siteMetadataResolver.resolveCastingApplicationStatusByCodeOrThrow(CASTING_APPLICATION_STATUS_NOT_PROCEEDING))
            .thenReturn(statusEntity(CASTING_APPLICATION_STATUS_NOT_PROCEEDING));
        when(siteMetadataResolver.resolveCastingApplicationStatusByCodeOrThrow(CASTING_APPLICATION_STATUS_PRESELECTED))
            .thenReturn(statusEntity(CASTING_APPLICATION_STATUS_PRESELECTED));
        when(castingApplicationRepository.setStatusIfOwned(eq(applicationId), eq(employerProfileId), any()))
            .thenReturn(1);

        assertDoesNotThrow(() -> service.notProceedingCastingApplication(applicationId));
        assertDoesNotThrow(() -> service.preselectCastingApplication(applicationId));
    }

    @Test
    void selected_thenBackToBlank_bothIndividualCallsSucceed() {
        when(siteMetadataResolver.resolveCastingApplicationStatusByCodeOrThrow(CASTING_APPLICATION_STATUS_SELECTED))
            .thenReturn(statusEntity(CASTING_APPLICATION_STATUS_SELECTED));
        when(siteMetadataResolver.resolveCastingApplicationStatusByCodeOrThrow(CASTING_APPLICATION_STATUS_BLANK))
            .thenReturn(statusEntity(CASTING_APPLICATION_STATUS_BLANK));
        when(castingApplicationRepository.setStatusIfOwned(eq(applicationId), eq(employerProfileId), any()))
            .thenReturn(1);

        assertDoesNotThrow(() -> service.selectCastingApplication(applicationId));
        assertDoesNotThrow(() -> service.blankCastingApplication(applicationId));
    }

    @Test
    void viewed_thenDirectlyToSelected_skippingPreselected_isAllowed() {
        when(siteMetadataResolver.resolveCastingApplicationStatusByCodeOrThrow(CASTING_APPLICATION_STATUS_VIEWED))
            .thenReturn(statusEntity(CASTING_APPLICATION_STATUS_VIEWED));
        when(siteMetadataResolver.resolveCastingApplicationStatusByCodeOrThrow(CASTING_APPLICATION_STATUS_SELECTED))
            .thenReturn(statusEntity(CASTING_APPLICATION_STATUS_SELECTED));
        when(castingApplicationRepository.setStatusIfOwned(eq(applicationId), eq(employerProfileId), any()))
            .thenReturn(1);

        assertDoesNotThrow(() -> service.viewCastingApplication(applicationId));
        assertDoesNotThrow(() -> service.selectCastingApplication(applicationId));
    }

    // ---- the only real guardrail: ownership, not transition validity ----

    @Test
    void statusChange_notOwnedByEmployer_throwsNotFoundOrForbidden_notATransitionError() {
        when(siteMetadataResolver.resolveCastingApplicationStatusByCodeOrThrow(CASTING_APPLICATION_STATUS_SELECTED))
            .thenReturn(statusEntity(CASTING_APPLICATION_STATUS_SELECTED));
        when(castingApplicationRepository.setStatusIfOwned(eq(applicationId), eq(employerProfileId), any()))
            .thenReturn(0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.selectCastingApplication(applicationId));

        assertEquals("applications.not_found_or_forbidden", exception.getMessage());
    }

    // ---- bulk update: mixed target regardless of each application's current status ----

    @Test
    void bulkSetStatus_toAnyStatus_isAllowedRegardlessOfCurrentStatuses() {
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        when(siteMetadataResolver.resolveCastingApplicationStatusByCodeOrThrow(CASTING_APPLICATION_STATUS_NOT_PROCEEDING))
            .thenReturn(statusEntity(CASTING_APPLICATION_STATUS_NOT_PROCEEDING));
        when(castingApplicationRepository.setStatusIfOwnedBulk(eq(ids), eq(employerProfileId), any()))
            .thenReturn(ids.size());

        BulkCastingApplicationStatusRequest request =
            new BulkCastingApplicationStatusRequest(ids, CASTING_APPLICATION_STATUS_NOT_PROCEEDING);

        assertDoesNotThrow(() -> service.bulkSetCastingApplicationsStatus(request));
    }

    @Test
    void bulkSetStatus_backToPreselectedAfterNotProceeding_isAllowed() {
        List<UUID> ids = List.of(UUID.randomUUID());
        when(siteMetadataResolver.resolveCastingApplicationStatusByCodeOrThrow(CASTING_APPLICATION_STATUS_PRESELECTED))
            .thenReturn(statusEntity(CASTING_APPLICATION_STATUS_PRESELECTED));
        when(castingApplicationRepository.setStatusIfOwnedBulk(eq(ids), eq(employerProfileId), any()))
            .thenReturn(1);

        BulkCastingApplicationStatusRequest request =
            new BulkCastingApplicationStatusRequest(ids, CASTING_APPLICATION_STATUS_PRESELECTED);

        assertDoesNotThrow(() -> service.bulkSetCastingApplicationsStatus(request));
    }

    @Test
    void bulkSetStatus_partialOwnership_throwsNotFoundOrForbidden_notATransitionError() {
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        when(siteMetadataResolver.resolveCastingApplicationStatusByCodeOrThrow(CASTING_APPLICATION_STATUS_SELECTED))
            .thenReturn(statusEntity(CASTING_APPLICATION_STATUS_SELECTED));
        when(castingApplicationRepository.setStatusIfOwnedBulk(eq(ids), eq(employerProfileId), any()))
            .thenReturn(1);

        BulkCastingApplicationStatusRequest request =
            new BulkCastingApplicationStatusRequest(ids, CASTING_APPLICATION_STATUS_SELECTED);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.bulkSetCastingApplicationsStatus(request));

        assertEquals("applications.not_found_or_forbidden", exception.getMessage());
    }
}
