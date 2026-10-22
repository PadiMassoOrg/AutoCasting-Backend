package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.request.AdminUserUpdateRequest;
import com.padimasso.autocasting.application.admin.mapper.AdminProfileMapper;
import com.padimasso.autocasting.application.admin.mapper.AdminUserMapper;
import com.padimasso.autocasting.application.admin.repository.AdminUserActivityRepository;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.email.service.EmailDeliveryService;
import com.padimasso.autocasting.application.email.service.TalentPhotoRemovedEmailService;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.history.dto.HistoryChangeEntry;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.application.talent.repository.MediaRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.MediaStorageService;
import com.padimasso.autocasting.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplUpdateUserDetailTest {

    private static final String EMAIL = "talent@example.com";

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
    private AdminUserActivityRepository adminUserActivityRepository;
    @Mock
    private AdminUserMapper adminUserMapper;
    @Mock
    private AdminProfileMapper adminProfileMapper;
    @Mock
    private HistoryService historyService;
    @Mock
    private EmailDeliveryService emailDeliveryService;
    @Mock
    private TalentPhotoRemovedEmailService talentPhotoRemovedEmailService;

    private AdminUserServiceImpl service;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        service = new AdminUserServiceImpl(
            userRepository,
            talentProfileRepository,
            mediaRepository,
            mediaStorageService,
            employerProfileRepository,
            adminUserActivityRepository,
            adminUserMapper,
            adminProfileMapper,
            historyService,
            emailDeliveryService,
            talentPhotoRemovedEmailService
        );
        user = UserEntity.builder().id(UUID.randomUUID()).email(EMAIL).build();
        when(userRepository.findByIdIncludingDeleted(user.getId())).thenReturn(Optional.of(user));
    }

    private AdminUserUpdateRequest request(Boolean emailingListSubscribed) {
        return new AdminUserUpdateRequest(EMAIL, null, false, false, emailingListSubscribed, "note");
    }

    @Test
    void updateUserDetail_unsubscribe_setsTheOptOutDateAndRecordsHistory() {
        service.updateUserDetail(user.getId(), request(false));

        assertNotNull(user.getMarketingEmailsUnsubscribedAt());
        verify(historyService).createHistoryEntry(
            eq(EntityType.USER), eq(user.getId()), eq("note"),
            eq(List.of(new HistoryChangeEntry("emailingListSubscribed", true, false)))
        );
    }

    @Test
    void updateUserDetail_resubscribe_clearsTheOptOutDate() {
        user.setMarketingEmailsUnsubscribedAt(LocalDateTime.now().minusDays(2));

        service.updateUserDetail(user.getId(), request(true));

        assertNull(user.getMarketingEmailsUnsubscribedAt());
    }

    @Test
    void updateUserDetail_withoutEmailingListValue_leavesItUntouched() {
        user.setMarketingEmailsUnsubscribedAt(LocalDateTime.now().minusDays(2));

        assertThrows(ApiException.class, () -> service.updateUserDetail(user.getId(), request(null)));
        assertNotNull(user.getMarketingEmailsUnsubscribedAt());
        verify(userRepository, never()).save(any());
    }
}
