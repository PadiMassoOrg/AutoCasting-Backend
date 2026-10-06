package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.request.AdminTalentProfileUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminTalentProfileResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminProfileMapper;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.dto.HistoryChangeEntry;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.talent.dto.request.ContactPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.MediaPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.CreditRequest;
import com.padimasso.autocasting.application.talent.dto.request.CreditUpsertRequest;
import com.padimasso.autocasting.application.talent.dto.request.SkillsPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.CreditResponse;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.BasicInfoService;
import com.padimasso.autocasting.application.talent.service.CharacteristicsService;
import com.padimasso.autocasting.application.talent.service.ContactService;
import com.padimasso.autocasting.application.talent.service.CreditService;
import com.padimasso.autocasting.application.talent.service.EducationService;
import com.padimasso.autocasting.application.talent.service.MediaService;
import com.padimasso.autocasting.application.talent.service.SocialMediaService;
import com.padimasso.autocasting.application.talent.service.TalentProfileService;
import com.padimasso.autocasting.config.AppProperties;
import com.padimasso.autocasting.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTalentProfileEditServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TalentProfileRepository talentProfileRepository;
    @Mock
    private BasicInfoService basicInfoService;
    @Mock
    private ContactService contactService;
    @Mock
    private SocialMediaService socialMediaService;
    @Mock
    private MediaService mediaService;
    @Mock
    private CharacteristicsService characteristicsService;
    @Mock
    private TalentProfileService talentProfileService;
    @Mock
    private CreditService creditService;
    @Mock
    private EducationService educationService;
    @Mock
    private AdminProfileMapper adminProfileMapper;
    @Mock
    private HistoryService historyService;
    @Mock
    private EntityManager entityManager;

    private final AppProperties appProperties = new AppProperties();

    private AdminTalentProfileEditServiceImpl service;

    private UUID userId;
    private UUID profileId;
    private TalentProfileEntity profile;

    @BeforeEach
    void setUp() {
        service = new AdminTalentProfileEditServiceImpl(
            userRepository,
            talentProfileRepository,
            basicInfoService,
            contactService,
            socialMediaService,
            mediaService,
            characteristicsService,
            talentProfileService,
            creditService,
            educationService,
            adminProfileMapper,
            historyService,
            entityManager,
            new ObjectMapper().findAndRegisterModules(),
            appProperties
        );
        appProperties.getSupabase().setUrl("https://abc.supabase.co");
        appProperties.getSupabase().setMediaBucket("profile-media-public");
        userId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        profile = TalentProfileEntity.builder().id(profileId).build();
    }

    private static AdminTalentProfileUpdateRequest request(
        ContactPatchRequest contact,
        SkillsPatchRequest skills,
        List<CreditUpsertRequest> credits
    ) {
        return new AdminTalentProfileUpdateRequest("fix typos", null, contact, null, null, null, skills, credits, null);
    }

    private static AdminTalentProfileResponse response(Set<SiteMetadataObject> skills, Set<CreditResponse> credits) {
        return new AdminTalentProfileResponse(
            null, null, false, null, null, null, null, null, null, skills, credits, Set.of(), null, null, null, null, null
        );
    }

    private void stubProfileFound() {
        when(userRepository.findByIdIncludingDeleted(userId)).thenReturn(Optional.of(UserEntity.builder().id(userId).build()));
        when(talentProfileRepository.findTalentProfileForAdminByUserId(userId)).thenReturn(Optional.of(profile));
    }

    @Test
    void updateTalentProfile_noSections_throwsBadRequestWithoutTouchingAnything() {
        var empty = request(null, null, null);

        assertThrows(ApiException.class, () -> service.updateTalentProfile(userId, empty));

        verifyNoInteractions(userRepository, talentProfileRepository, historyService, contactService);
    }

    @Test
    void updateTalentProfile_unknownUser_throwsNotFound() {
        when(userRepository.findByIdIncludingDeleted(userId)).thenReturn(Optional.empty());

        assertThrows(
            ApiException.class,
            () -> service.updateTalentProfile(userId, request(new ContactPatchRequest("+34600000000"), null, null))
        );

        verifyNoInteractions(historyService, contactService);
    }

    @Test
    void updateTalentProfile_appliesOnlyProvidedSections() {
        stubProfileFound();
        when(adminProfileMapper.toTalentProfileResponse(any())).thenReturn(response(Set.of(), Set.of()));
        var contact = new ContactPatchRequest("+34600000000");

        service.updateTalentProfile(userId, request(contact, null, null));

        verify(contactService).patchContact(profile, contact);
        verify(talentProfileService, never()).patchSkills(any(), any());
        verify(creditService, never()).replaceCredits(any(), any());
        verify(basicInfoService, never()).patchBasicInfo(any(), any());
        verify(educationService, never()).replaceEducation(any(), any());
    }

    @Test
    void updateTalentProfile_unchangedResult_doesNotRecordHistory() {
        stubProfileFound();
        when(adminProfileMapper.toTalentProfileResponse(any())).thenReturn(response(Set.of(), Set.of()));

        service.updateTalentProfile(userId, request(new ContactPatchRequest("+34600000000"), null, null));

        verify(historyService, never()).createHistoryEntry(any(), any(), any(), any());
    }

    @Test
    void updateTalentProfile_flushesAndClearsBeforeRereadingAndRecordsHistoryWithReason() {
        stubProfileFound();
        var skillId = UUID.randomUUID();
        var addedSkill = new SiteMetadataObject(skillId, "sitemetadata.skill.acting", "skill");
        when(adminProfileMapper.toTalentProfileResponse(any()))
            .thenReturn(response(Set.of(), Set.of()))
            .thenReturn(response(Set.of(addedSkill), Set.of()));
        var skills = new SkillsPatchRequest(Set.of(skillId));
        var credits = List.of(new CreditUpsertRequest(null, new CreditRequest(UUID.randomUUID(), "p", "pr", "r", "2024")));

        service.updateTalentProfile(userId, request(null, skills, credits));

        var order = inOrder(talentProfileService, creditService, entityManager, talentProfileRepository, historyService);
        order.verify(talentProfileService).patchSkills(profile, skills);
        order.verify(creditService).replaceCredits(profile, credits);
        order.verify(entityManager).flush();
        order.verify(entityManager).clear();
        order.verify(talentProfileRepository).findTalentProfileForAdminByUserId(userId);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<HistoryChangeEntry>> changes = ArgumentCaptor.forClass(List.class);
        verify(historyService).createHistoryEntry(eq(EntityType.TALENT_PROFILE), eq(profileId), eq("fix typos"), changes.capture());
        assertEquals(List.of("skills.added"), changes.getValue().stream().map(HistoryChangeEntry::fieldKey).toList());
    }

    @Test
    void updateTalentProfile_snapshotsBeforeStateBeforeClearingThePersistenceContext() {
        stubProfileFound();
        var detached = new AtomicBoolean(false);
        doAnswer(invocation -> {
            detached.set(true);
            return null;
        }).when(entityManager).clear();
        // Stands in for a lazy Hibernate collection: unreadable once the context is cleared.
        Set<SiteMetadataObject> lazySkills = new AbstractSet<>() {
            @Override
            public Iterator<SiteMetadataObject> iterator() {
                if (detached.get()) throw new IllegalStateException("could not initialize proxy - no Session");
                return Collections.emptyIterator();
            }

            @Override
            public int size() {
                if (detached.get()) throw new IllegalStateException("could not initialize proxy - no Session");
                return 0;
            }
        };
        when(adminProfileMapper.toTalentProfileResponse(any()))
            .thenReturn(response(lazySkills, Set.of()))
            .thenReturn(response(Set.of(), Set.of()));

        service.updateTalentProfile(userId, request(null, new SkillsPatchRequest(Set.of(UUID.randomUUID())), null));

        verify(entityManager).clear();
    }

    @Test
    void updateTalentProfile_rejectsAPhotoThatIsNotASupabaseUploadAndAppliesNothing() {
        stubProfileFound();
        var media = new MediaPatchRequest(
            JsonNullable.of("https://example.com/me.jpg"), JsonNullable.undefined(), null, JsonNullable.undefined(), JsonNullable.undefined()
        );
        var request = new AdminTalentProfileUpdateRequest("reason", null, new ContactPatchRequest("+34600000000"), null, media, null, null, null, null);

        assertThrows(ApiException.class, () -> service.updateTalentProfile(userId, request));

        verify(mediaService, never()).patchMedia(any(), any());
        verify(contactService, never()).patchContact(any(), any());
        verify(historyService, never()).createHistoryEntry(any(), any(), any(), any());
    }
}
