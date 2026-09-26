package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.request.AdminRemoveTalentMediaRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminTalentMediaSlot;
import com.padimasso.autocasting.application.admin.mapper.AdminProfileMapper;
import com.padimasso.autocasting.application.admin.mapper.AdminUserMapper;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.application.talent.model.MediaEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.MediaRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.MediaStorageService;
import com.padimasso.autocasting.application.talent.service.TalentWelcomeEmailService;
import com.padimasso.autocasting.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplRemoveTalentMediaTest {

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
    private UUID profileId;
    private TalentProfileEntity profile;
    private MediaEntity media;

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
        profileId = UUID.randomUUID();
        media = MediaEntity.builder()
            .headshotImageUrl("https://example.com/headshot.jpg")
            .fullBodyImageUrl("https://example.com/full-body.jpg")
            .introductionVideoUrl("https://example.com/intro.mp4")
            .showReelVideoUrl("https://example.com/show-reel.mp4")
            .otherPicturesUrl(new ArrayList<>(List.of("https://example.com/p0.jpg", "https://example.com/p1.jpg")))
            .build();
        profile = TalentProfileEntity.builder().id(profileId).media(media).build();
    }

    @Test
    void removeTalentMedia_headshot_nullsFieldAndRecordsHistory() {
        when(talentProfileRepository.findTalentProfileForAdminByUserId(userId)).thenReturn(Optional.of(profile));

        service.removeTalentMedia(userId, AdminTalentMediaSlot.HEADSHOT, null, new AdminRemoveTalentMediaRequest("inappropriate content"));

        assertNull(media.getHeadshotImageUrl());
        verify(mediaRepository).save(media);
        verify(mediaStorageService).deleteByPublicUrl("https://example.com/headshot.jpg");
        verify(historyService).createHistoryEntry(
            eq(EntityType.TALENT_PROFILE),
            eq(profileId),
            eq("headshot: inappropriate content"),
            eq("headshotImageUrl: removed")
        );
    }

    @Test
    void removeTalentMedia_otherPicture_nullsSlotWithoutShiftingList() {
        when(talentProfileRepository.findTalentProfileForAdminByUserId(userId)).thenReturn(Optional.of(profile));

        service.removeTalentMedia(userId, AdminTalentMediaSlot.OTHER_PICTURE, 0, new AdminRemoveTalentMediaRequest("reason"));

        assertEquals(2, media.getOtherPicturesUrl().size());
        assertNull(media.getOtherPicturesUrl().get(0));
        assertEquals("https://example.com/p1.jpg", media.getOtherPicturesUrl().get(1));
        verify(historyService).createHistoryEntry(
            eq(EntityType.TALENT_PROFILE),
            eq(profileId),
            eq("other picture #1: reason"),
            eq("otherPicturesUrl[0]: removed")
        );
    }

    @Test
    void removeTalentMedia_otherPicture_indexOutOfRange_throwsBadRequest() {
        when(talentProfileRepository.findTalentProfileForAdminByUserId(userId)).thenReturn(Optional.of(profile));

        assertThrows(ApiException.class, () ->
            service.removeTalentMedia(userId, AdminTalentMediaSlot.OTHER_PICTURE, 5, new AdminRemoveTalentMediaRequest("reason"))
        );
        verify(mediaRepository, never()).save(any());
        verify(mediaStorageService, never()).deleteByPublicUrl(any());
        verify(historyService, never()).createHistoryEntry(any(), any(), any(), any());
    }

    @Test
    void removeTalentMedia_otherPicture_missingIndex_throwsBadRequest() {
        when(talentProfileRepository.findTalentProfileForAdminByUserId(userId)).thenReturn(Optional.of(profile));

        assertThrows(ApiException.class, () ->
            service.removeTalentMedia(userId, AdminTalentMediaSlot.OTHER_PICTURE, null, new AdminRemoveTalentMediaRequest("reason"))
        );
    }

    @Test
    void removeTalentMedia_profileNotFound_throwsNotFound() {
        when(talentProfileRepository.findTalentProfileForAdminByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () ->
            service.removeTalentMedia(userId, AdminTalentMediaSlot.HEADSHOT, null, new AdminRemoveTalentMediaRequest("reason"))
        );
    }

    @Test
    void removeTalentMedia_fullBody_recordsPlainRemovedChangeWithoutTheOldUrl() {
        when(talentProfileRepository.findTalentProfileForAdminByUserId(userId)).thenReturn(Optional.of(profile));
        ArgumentCaptor<Object> changesCaptor = ArgumentCaptor.forClass(Object.class);

        service.removeTalentMedia(userId, AdminTalentMediaSlot.FULL_BODY, null, new AdminRemoveTalentMediaRequest("reason"));

        verify(historyService, times(1)).createHistoryEntry(
            eq(EntityType.TALENT_PROFILE),
            eq(profileId),
            eq("full body photo: reason"),
            changesCaptor.capture()
        );
        assertEquals("fullBodyImageUrl: removed", changesCaptor.getValue());
        assertNull(media.getFullBodyImageUrl());
    }
}
