package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.response.AdminEmployerProfileResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminTalentProfileResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminProfileMapper;
import com.padimasso.autocasting.application.admin.mapper.AdminUserMapper;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.MediaRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.MediaStorageService;
import com.padimasso.autocasting.application.talent.service.TalentWelcomeEmailService;
import com.padimasso.autocasting.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.PROPOSALS_SYSTEM_USER_ID;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplProfileReadTest {

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
    private AdminUserMapper adminUserMapper;
    @Mock
    private AdminProfileMapper adminProfileMapper;
    @Mock
    private HistoryService historyService;
    @Mock
    private TalentWelcomeEmailService talentWelcomeEmailService;

    private AdminUserServiceImpl service;

    private UUID userId;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        service = new AdminUserServiceImpl(
            userRepository,
            talentProfileRepository,
            mediaRepository,
            mediaStorageService,
            employerProfileRepository,
            adminUserMapper,
            adminProfileMapper,
            historyService,
            talentWelcomeEmailService
        );

        userId = UUID.randomUUID();
        user = UserEntity.builder().id(userId).build();
    }

    @Test
    void getTalentProfileForAdmin_returnsMappedAdminResponse() {
        var profile = TalentProfileEntity.builder().id(UUID.randomUUID()).user(user).build();
        var expected = mock(AdminTalentProfileResponse.class);
        when(userRepository.findByIdIncludingDeleted(userId)).thenReturn(Optional.of(user));
        when(talentProfileRepository.findTalentProfileForAdminByUserId(userId)).thenReturn(Optional.of(profile));
        when(adminProfileMapper.toTalentProfileResponse(profile)).thenReturn(expected);

        assertSame(expected, service.getTalentProfileForAdmin(userId));
    }

    @Test
    void getTalentProfileForAdmin_unknownUser_throwsNotFound() {
        when(userRepository.findByIdIncludingDeleted(userId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.getTalentProfileForAdmin(userId));
        verify(talentProfileRepository, never()).findTalentProfileForAdminByUserId(any());
    }

    @Test
    void getTalentProfileForAdmin_systemUser_throwsNotFound() {
        var systemUser = UserEntity.builder().id(PROPOSALS_SYSTEM_USER_ID).build();
        when(userRepository.findByIdIncludingDeleted(PROPOSALS_SYSTEM_USER_ID)).thenReturn(Optional.of(systemUser));

        assertThrows(ApiException.class, () -> service.getTalentProfileForAdmin(PROPOSALS_SYSTEM_USER_ID));
        verify(talentProfileRepository, never()).findTalentProfileForAdminByUserId(any());
    }

    @Test
    void getTalentProfileForAdmin_missingProfile_throwsNotFound() {
        when(userRepository.findByIdIncludingDeleted(userId)).thenReturn(Optional.of(user));
        when(talentProfileRepository.findTalentProfileForAdminByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.getTalentProfileForAdmin(userId));
        verify(adminProfileMapper, never()).toTalentProfileResponse(any());
    }

    @Test
    void getEmployerProfileForAdmin_returnsMappedAdminResponse() {
        var profile = EmployerProfileEntity.builder().id(UUID.randomUUID()).user(user).build();
        var expected = mock(AdminEmployerProfileResponse.class);
        when(userRepository.findByIdIncludingDeleted(userId)).thenReturn(Optional.of(user));
        when(employerProfileRepository.findEmployerProfileForAdminByUserId(userId)).thenReturn(Optional.of(profile));
        when(adminProfileMapper.toEmployerProfileResponse(profile)).thenReturn(expected);

        assertSame(expected, service.getEmployerProfileForAdmin(userId));
    }

    @Test
    void getEmployerProfileForAdmin_systemUser_throwsNotFound() {
        var systemUser = UserEntity.builder().id(PROPOSALS_SYSTEM_USER_ID).build();
        when(userRepository.findByIdIncludingDeleted(PROPOSALS_SYSTEM_USER_ID)).thenReturn(Optional.of(systemUser));

        assertThrows(ApiException.class, () -> service.getEmployerProfileForAdmin(PROPOSALS_SYSTEM_USER_ID));
        verify(employerProfileRepository, never()).findEmployerProfileForAdminByUserId(any());
    }

    @Test
    void getEmployerProfileForAdmin_missingProfile_throwsNotFound() {
        when(userRepository.findByIdIncludingDeleted(userId)).thenReturn(Optional.of(user));
        when(employerProfileRepository.findEmployerProfileForAdminByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.getEmployerProfileForAdmin(userId));
        verify(adminProfileMapper, never()).toEmployerProfileResponse(any());
    }
}
