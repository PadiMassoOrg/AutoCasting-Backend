package com.padimasso.autocasting.application.applications.service.impl;

import com.padimasso.autocasting.application.applications.mapper.CastingApplicationMapper;
import com.padimasso.autocasting.application.applications.repository.CastingApplicationRepository;
import com.padimasso.autocasting.application.applications.repository.CastingApplicationRequirementSubmissionRepository;
import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.context.EmployerContext;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.util.CastingDeadlines;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.talent.model.MediaEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_CLOSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PAUSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.APPLICATIONS_ALREADY_APPLIED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_DEADLINE_PASSED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CastingApplicationServiceImplApplyTest {

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
    private UUID roleId;

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
        roleId = UUID.randomUUID();

        var user = UserEntity.builder().id(UUID.randomUUID()).build();
        var media = MediaEntity.builder()
            .headshotImageUrl("https://example.com/headshot.jpg")
            .fullBodyImageUrl("https://example.com/full-body.jpg")
            .build();
        var profile = TalentProfileEntity.builder().id(UUID.randomUUID()).media(media).build();
        when(authContext.getCurrentUserOrThrow()).thenReturn(user);
        when(talentProfileRepository.findByUserId(user.getId())).thenReturn(Optional.of(profile));
    }

    private void givenRoleInCasting(String statusCode, LocalDate deadline) {
        var status = new CastingStatusOptionEntity();
        status.setStringCode(statusCode);
        var casting = CastingEntity.builder().status(status).applicationDeadline(deadline).build();
        var role = CastingRoleEntity.builder().id(roleId).casting(casting).build();
        when(castingRoleRepository.findByIdAndDeletedFalse(roleId)).thenReturn(Optional.of(role));
    }

    @Test
    void apply_publishedCastingPastDeadline_isRejectedWithoutSaving() {
        givenRoleInCasting(CASTING_STATUS_PUBLISHED, CastingDeadlines.today().minusDays(1));

        var ex = assertThrows(IllegalStateException.class, () -> service.apply(roleId, null));

        assertEquals(CASTINGS_DEADLINE_PASSED, ex.getMessage());
        verify(castingApplicationRepository, never()).save(any());
    }

    @Test
    void apply_publishedCastingWithDeadlineToday_passesTheCastingCheck() {
        givenRoleInCasting(CASTING_STATUS_PUBLISHED, CastingDeadlines.today());
        when(castingApplicationRepository.existsByCastingRoleIdAndTalentProfileId(any(), any())).thenReturn(true);

        var ex = assertThrows(IllegalStateException.class, () -> service.apply(roleId, null));

        assertEquals(APPLICATIONS_ALREADY_APPLIED, ex.getMessage());
    }

    @Test
    void apply_closedCasting_isRejected() {
        givenRoleInCasting(CASTING_STATUS_CLOSED, CastingDeadlines.today().plusDays(5));

        var ex = assertThrows(IllegalStateException.class, () -> service.apply(roleId, null));

        assertEquals(CASTINGS_DEADLINE_PASSED, ex.getMessage());
        verify(castingApplicationRepository, never()).save(any());
    }

    @Test
    void apply_pausedCasting_isRejectedAsNotFound() {
        givenRoleInCasting(CASTING_STATUS_PAUSED, CastingDeadlines.today().plusDays(5));

        var ex = assertThrows(IllegalStateException.class, () -> service.apply(roleId, null));

        assertEquals(CASTINGS_NOT_FOUND, ex.getMessage());
    }

    @Test
    void apply_openCasting_passesTheCastingCheck() {
        givenRoleInCasting(CASTING_STATUS_PUBLISHED, CastingDeadlines.today().plusDays(1));
        when(castingApplicationRepository.existsByCastingRoleIdAndTalentProfileId(any(), any())).thenReturn(true);

        var ex = assertThrows(IllegalStateException.class, () -> service.apply(roleId, null));

        assertEquals(APPLICATIONS_ALREADY_APPLIED, ex.getMessage());
    }
}
