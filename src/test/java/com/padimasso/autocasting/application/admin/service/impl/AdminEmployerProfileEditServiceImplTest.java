package com.padimasso.autocasting.application.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.padimasso.autocasting.application.admin.dto.request.AdminEmployerProfileUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminEmployerProfileResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminProfileMapper;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.employer.dto.request.EmployerBasicInfoPatchRequest;
import com.padimasso.autocasting.application.employer.dto.response.EmployerBasicInfoResponse;
import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.employer.service.EmployerBasicInfoService;
import com.padimasso.autocasting.application.history.dto.HistoryChangeEntry;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.application.talent.dto.request.SocialMediaLinkDto;
import com.padimasso.autocasting.application.talent.dto.request.SocialMediaPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.SocialMediaLinkResponse;
import com.padimasso.autocasting.application.talent.dto.response.SocialMediaResponse;
import com.padimasso.autocasting.application.talent.service.SocialMediaService;
import com.padimasso.autocasting.config.AppProperties;
import com.padimasso.autocasting.exception.ApiException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminEmployerProfileEditServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EmployerProfileRepository employerProfileRepository;
    @Mock
    private EmployerBasicInfoService employerBasicInfoService;
    @Mock
    private SocialMediaService socialMediaService;
    @Mock
    private AdminProfileMapper adminProfileMapper;
    @Mock
    private HistoryService historyService;
    @Mock
    private EntityManager entityManager;

    private final AppProperties appProperties = new AppProperties();
    private AdminEmployerProfileEditServiceImpl service;
    private UUID userId;
    private UUID profileId;
    private EmployerProfileEntity profile;

    @BeforeEach
    void setUp() {
        service = new AdminEmployerProfileEditServiceImpl(
            userRepository, employerProfileRepository, employerBasicInfoService, socialMediaService,
            adminProfileMapper, historyService, entityManager, new ObjectMapper().findAndRegisterModules(), appProperties
        );
        appProperties.getSupabase().setUrl("https://abc.supabase.co");
        appProperties.getSupabase().setMediaBucket("profile-media-public");
        userId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        profile = EmployerProfileEntity.builder().id(profileId).basicInfo(EmployerBasicInfoEntity.builder().id(UUID.randomUUID()).build()).build();
    }

    private static EmployerBasicInfoPatchRequest basicInfo(String companyName, JsonNullable<String> imageUrl) {
        return new EmployerBasicInfoPatchRequest(
            JsonNullable.of(companyName), JsonNullable.undefined(), null, JsonNullable.undefined(),
            imageUrl, JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined()
        );
    }

    private AdminEmployerProfileResponse response(String companyName, List<SocialMediaLinkResponse> links) {
        var info = new EmployerBasicInfoResponse(
            UUID.randomUUID(), companyName, null, null, null, null, null, null, null, new SocialMediaResponse(links, null)
        );
        return new AdminEmployerProfileResponse(profileId, "slug", null, info, null, null, null, null);
    }

    private void stubFound() {
        when(userRepository.findByIdIncludingDeleted(userId)).thenReturn(Optional.of(UserEntity.builder().id(userId).build()));
        when(employerProfileRepository.findEmployerProfileForAdminByUserId(userId)).thenReturn(Optional.of(profile));
    }

    @Test
    void noSections_throwsBadRequestWithoutTouchingAnything() {
        var empty = new AdminEmployerProfileUpdateRequest("reason", null, null);

        assertThrows(ApiException.class, () -> service.updateEmployerProfile(userId, empty));

        verifyNoInteractions(userRepository, employerProfileRepository, historyService, employerBasicInfoService);
    }

    @Test
    void unknownUser_throwsNotFound() {
        when(userRepository.findByIdIncludingDeleted(userId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.updateEmployerProfile(userId, new AdminEmployerProfileUpdateRequest("r", basicInfo("A", JsonNullable.undefined()), null)));

        verifyNoInteractions(historyService, employerBasicInfoService);
    }

    @Test
    void appliesOnlyTheProvidedSectionsAndRecordsOnlyWhatChanged() {
        stubFound();
        when(adminProfileMapper.toEmployerProfileResponse(any()))
            .thenReturn(response("Old", List.of()))
            .thenReturn(response("New", List.of()));
        var request = new AdminEmployerProfileUpdateRequest("fix name", basicInfo("New", JsonNullable.undefined()), null);

        service.updateEmployerProfile(userId, request);

        verify(employerBasicInfoService).patchBasicInfo(profile, request.basicInfo());
        verify(socialMediaService, never()).patchEmployerSocialMedia(any(), any());
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<HistoryChangeEntry>> changes = ArgumentCaptor.forClass(List.class);
        verify(historyService).createHistoryEntry(eq(EntityType.EMPLOYER_PROFILE), eq(profileId), eq("fix name"), changes.capture());
        assertEquals(List.of("basicInfo.companyName"), changes.getValue().stream().map(HistoryChangeEntry::fieldKey).toList());
        assertEquals("Old", changes.getValue().get(0).previousValue());
        assertEquals("New", changes.getValue().get(0).newValue());
    }

    @Test
    void socialLinksAreReportedAsTheirOwnSectionNotInsideBasicInfo() {
        stubFound();
        var link = new SocialMediaLinkResponse(UUID.randomUUID(), "sitemetadata.social_media.instagram", "https://ig.com/a");
        when(adminProfileMapper.toEmployerProfileResponse(any()))
            .thenReturn(response("Same", List.of()))
            .thenReturn(response("Same", List.of(link)));
        var request = new AdminEmployerProfileUpdateRequest("add link", null, new SocialMediaPatchRequest(List.of(new SocialMediaLinkDto(link.optionId(), link.url()))));

        service.updateEmployerProfile(userId, request);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<HistoryChangeEntry>> changes = ArgumentCaptor.forClass(List.class);
        verify(historyService).createHistoryEntry(any(), any(), any(), changes.capture());
        assertEquals(List.of("socialMedia.links.added"), changes.getValue().stream().map(HistoryChangeEntry::fieldKey).toList());
    }

    @Test
    void rejectsALogoThatIsNotASupabaseUploadAndAppliesNothing() {
        stubFound();
        var request = new AdminEmployerProfileUpdateRequest("r", basicInfo("A", JsonNullable.of("https://example.com/logo.png")), null);

        assertThrows(ApiException.class, () -> service.updateEmployerProfile(userId, request));

        verify(employerBasicInfoService, never()).patchBasicInfo(any(), any());
        verify(historyService, never()).createHistoryEntry(any(), any(), any(), any());
    }

    @Test
    void flushesAndClearsBeforeReReadingTheProfile() {
        stubFound();
        when(adminProfileMapper.toEmployerProfileResponse(any())).thenReturn(response("A", List.of()));

        service.updateEmployerProfile(userId, new AdminEmployerProfileUpdateRequest("r", basicInfo("B", JsonNullable.undefined()), null));

        var order = inOrder(employerBasicInfoService, entityManager, employerProfileRepository);
        order.verify(employerBasicInfoService).patchBasicInfo(any(), any());
        order.verify(entityManager).flush();
        order.verify(entityManager).clear();
        order.verify(employerProfileRepository).findEmployerProfileForAdminByUserId(userId);
    }
}
