package com.padimasso.autocasting.application.admin.mapper;

import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.common.model.AuditableEntity;
import com.padimasso.autocasting.application.employer.dto.response.EmployerBasicInfoResponse;
import com.padimasso.autocasting.application.employer.mapper.EmployerProfileMapper;
import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.plan.model.PlanEntity;
import com.padimasso.autocasting.application.talent.dto.response.BasicInfoResponse;
import com.padimasso.autocasting.application.talent.dto.response.TalentProfileResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.model.MediaEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminProfileMapperTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1, 10, 9, 0);
    private static final LocalDateTime MODIFIED_AT = LocalDateTime.of(2026, 2, 20, 18, 30);
    private static final LocalDateTime LAST_SAVED_AT = LocalDateTime.of(2026, 3, 5, 12, 0);

    @Mock
    private TalentProfileMapper talentProfileMapper;
    @Mock
    private EmployerProfileMapper employerProfileMapper;

    private AdminProfileMapper mapper;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        mapper = new AdminProfileMapper(talentProfileMapper, employerProfileMapper);
        user = UserEntity.builder()
            .id(UUID.randomUUID())
            .talentOnboardingStatus(OnboardingStatus.COMPLETED)
            .employerOnboardingStatus(OnboardingStatus.IN_PROGRESS)
            .build();
    }

    @Test
    void toTalentProfileResponse_copiesSectionsAndAddsAdminFields() {
        var media = MediaEntity.builder()
            .headshotImageUrl("https://example.com/headshot.jpg")
            .fullBodyImageUrl("https://example.com/full-body.jpg")
            .build();
        var profile = talentProfile(media);
        var basicInfo = mock(BasicInfoResponse.class);
        var sections = new TalentProfileResponse(
            profile.getId(), "LOCAL", "plan.free", "jane-doe", basicInfo, null, null, null, null,
            Set.of(), Set.of(), Set.of(), LAST_SAVED_AT
        );
        when(talentProfileMapper.toProfileResponse(profile, user)).thenReturn(sections);

        var response = mapper.toTalentProfileResponse(profile);

        assertEquals(profile.getId(), response.id());
        assertEquals("jane-doe", response.publicSlug());
        assertEquals(LAST_SAVED_AT, response.lastSavedAt());
        assertSame(basicInfo, response.basicInfo());
        assertTrue(response.visibleInCatalog());
        assertEquals(OnboardingStatus.COMPLETED, response.onboardingStatus());
        assertEquals(CREATED_AT, response.createdAt());
        assertEquals("creator@example.com", response.createdBy());
        assertEquals(MODIFIED_AT, response.modifiedAt());
        assertEquals("editor@example.com", response.modifiedBy());
    }

    @Test
    void toTalentProfileResponse_missingRequiredPhotos_isNotVisibleInCatalog() {
        var profile = talentProfile(MediaEntity.builder().headshotImageUrl("https://example.com/headshot.jpg").build());
        when(talentProfileMapper.toProfileResponse(profile, user)).thenReturn(emptySections(profile.getId()));

        assertFalse(mapper.toTalentProfileResponse(profile).visibleInCatalog());
    }

    @Test
    void toTalentProfileResponse_suspendedUser_isNotVisibleInCatalog() {
        user.setSuspended(true);
        var profile = talentProfile(MediaEntity.builder()
            .headshotImageUrl("https://example.com/headshot.jpg")
            .fullBodyImageUrl("https://example.com/full-body.jpg")
            .build());
        when(talentProfileMapper.toProfileResponse(profile, user)).thenReturn(emptySections(profile.getId()));

        assertFalse(mapper.toTalentProfileResponse(profile).visibleInCatalog());
    }

    @Test
    void toEmployerProfileResponse_mapsBasicInfoOnboardingAndAudit() {
        var basicInfoEntity = EmployerBasicInfoEntity.builder().id(UUID.randomUUID()).build();
        var profile = EmployerProfileEntity.builder()
            .id(UUID.randomUUID())
            .user(user)
            .plan(PlanEntity.builder().allowsCustomSlug(false).build())
            .defaultSlug("acme-studio")
            .basicInfo(basicInfoEntity)
            .build();
        applyAudit(profile);
        var basicInfo = mock(EmployerBasicInfoResponse.class);
        when(employerProfileMapper.toBasicInfoResponse(basicInfoEntity)).thenReturn(basicInfo);

        var response = mapper.toEmployerProfileResponse(profile);

        assertEquals(profile.getId(), response.id());
        assertEquals("acme-studio", response.publicSlug());
        assertEquals(OnboardingStatus.IN_PROGRESS, response.onboardingStatus());
        assertSame(basicInfo, response.basicInfo());
        assertEquals(CREATED_AT, response.createdAt());
        assertEquals("creator@example.com", response.createdBy());
        assertEquals(MODIFIED_AT, response.modifiedAt());
        assertEquals("editor@example.com", response.modifiedBy());
    }

    private TalentProfileEntity talentProfile(MediaEntity media) {
        var profile = TalentProfileEntity.builder().id(UUID.randomUUID()).user(user).media(media).build();
        applyAudit(profile);
        return profile;
    }

    private static void applyAudit(AuditableEntity entity) {
        entity.setCreatedAt(CREATED_AT);
        entity.setCreatedBy("creator@example.com");
        entity.setModifiedAt(MODIFIED_AT);
        entity.setModifiedBy("editor@example.com");
    }

    private static TalentProfileResponse emptySections(UUID profileId) {
        return new TalentProfileResponse(
            profileId, null, null, null, null, null, null, null, null, Set.of(), Set.of(), Set.of(), null
        );
    }
}
