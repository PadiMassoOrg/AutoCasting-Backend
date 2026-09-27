package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.mapper.AdminProfileMapper;
import com.padimasso.autocasting.application.admin.mapper.AdminUserMapper;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.application.talent.model.MediaEntity;
import com.padimasso.autocasting.application.talent.model.ProfileSocialMediaLinkEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.MediaRepository;
import com.padimasso.autocasting.application.talent.repository.ProfileSocialMediaLinkRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.MediaStorageService;
import com.padimasso.autocasting.application.talent.service.TalentWelcomeEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplListUsersTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 1, 1, 10, 0);

    @Mock
    private UserRepository userRepository;
    @Mock
    private TalentProfileRepository talentProfileRepository;
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private MediaStorageService mediaStorageService;
    @Mock
    private EmployerProfileRepository employerProfileRepository;
    @Mock
    private ProfileSocialMediaLinkRepository socialMediaLinkRepository;
    @Mock
    private AdminUserMapper adminUserMapper;
    @Mock
    private AdminProfileMapper adminProfileMapper;
    @Mock
    private HistoryService historyService;
    @Mock
    private TalentWelcomeEmailService talentWelcomeEmailService;

    private AdminUserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AdminUserServiceImpl(
            userRepository,
            talentProfileRepository,
            mediaRepository,
            mediaStorageService,
            employerProfileRepository,
            socialMediaLinkRepository,
            adminUserMapper,
            adminProfileMapper,
            historyService,
            talentWelcomeEmailService
        );
    }

    @SuppressWarnings("unchecked")
    private void givenUsers(UserEntity... users) {
        when(userRepository.findAllIncludingDeleted(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(users)));
    }

    @Test
    void listUsers_lastSavedAt_isTheLatestChangeAcrossTalentAndEmployerProfiles() {
        var user = UserEntity.builder().id(UUID.randomUUID()).build();
        givenUsers(user);

        var talent = TalentProfileEntity.builder().id(UUID.randomUUID()).user(user).media(MediaEntity.builder().build()).build();
        talent.setModifiedAt(BASE.plusDays(2));
        var talentLink = ProfileSocialMediaLinkEntity.builder().talentProfile(talent).build();
        talentLink.setModifiedAt(BASE.plusDays(4));

        var employerBasicInfo = EmployerBasicInfoEntity.builder().id(UUID.randomUUID()).build();
        employerBasicInfo.setModifiedAt(BASE.plusDays(7));
        var employer = EmployerProfileEntity.builder().id(UUID.randomUUID()).user(user).basicInfo(employerBasicInfo).build();
        employer.setModifiedAt(BASE);

        when(talentProfileRepository.findAllWithSectionsByUserIdInForAdmin(anyList())).thenReturn(List.of(talent));
        when(employerProfileRepository.findAllByUserIdInForAdmin(anyList())).thenReturn(List.of(employer));
        when(socialMediaLinkRepository.findAllByTalentProfileIdIn(anyList())).thenReturn(List.of(talentLink));
        when(socialMediaLinkRepository.findAllByEmployerBasicInfoIdIn(anyList())).thenReturn(List.of());

        service.listUsers(0, 8, null, false);

        verify(adminUserMapper).toRowResponse(eq(user), isNull(), isNull(), isNull(), eq(BASE.plusDays(7)));
    }

    @Test
    void listUsers_talentLinkChange_canBeTheLatest() {
        var user = UserEntity.builder().id(UUID.randomUUID()).build();
        givenUsers(user);

        var talent = TalentProfileEntity.builder().id(UUID.randomUUID()).user(user).media(MediaEntity.builder().build()).build();
        talent.setModifiedAt(BASE);
        var talentLink = ProfileSocialMediaLinkEntity.builder().talentProfile(talent).build();
        talentLink.setModifiedAt(BASE.plusDays(10));

        when(talentProfileRepository.findAllWithSectionsByUserIdInForAdmin(anyList())).thenReturn(List.of(talent));
        when(employerProfileRepository.findAllByUserIdInForAdmin(anyList())).thenReturn(List.of());
        when(socialMediaLinkRepository.findAllByTalentProfileIdIn(anyList())).thenReturn(List.of(talentLink));

        service.listUsers(0, 8, null, false);

        verify(adminUserMapper).toRowResponse(eq(user), isNull(), isNull(), isNull(), eq(BASE.plusDays(10)));
    }

    @Test
    void listUsers_userWithoutProfiles_hasNoLastSavedAt() {
        var user = UserEntity.builder().id(UUID.randomUUID()).build();
        givenUsers(user);
        when(talentProfileRepository.findAllWithSectionsByUserIdInForAdmin(anyList())).thenReturn(List.of());
        when(employerProfileRepository.findAllByUserIdInForAdmin(anyList())).thenReturn(List.of());

        service.listUsers(0, 8, null, false);

        verify(adminUserMapper).toRowResponse(eq(user), isNull(), isNull(), isNull(), isNull());
    }
}
