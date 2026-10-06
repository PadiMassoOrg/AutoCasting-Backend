package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.request.AdminTalentProfileUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminTalentProfileResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminProfileMapper;
import com.padimasso.autocasting.application.admin.service.AdminTalentProfileEditService;
import com.padimasso.autocasting.application.admin.util.ProfileChangeDiff;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.dto.HistoryChangeEntry;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.BasicInfoService;
import com.padimasso.autocasting.application.talent.service.CharacteristicsService;
import com.padimasso.autocasting.application.talent.service.ContactService;
import com.padimasso.autocasting.application.talent.service.CreditService;
import com.padimasso.autocasting.application.talent.service.EducationService;
import com.padimasso.autocasting.application.talent.service.MediaService;
import com.padimasso.autocasting.application.talent.service.SocialMediaService;
import com.padimasso.autocasting.application.talent.service.TalentProfileService;
import com.padimasso.autocasting.application.talent.util.TalentMediaUrlPolicy;
import com.padimasso.autocasting.config.AppProperties;
import com.padimasso.autocasting.exception.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.PROPOSALS_SYSTEM_USER_ID;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.ADMIN_USER_UPDATE_NO_CHANGES;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AdminTalentProfileEditServiceImpl implements AdminTalentProfileEditService {

    private final UserRepository userRepository;
    private final TalentProfileRepository talentProfileRepository;
    private final BasicInfoService basicInfoService;
    private final ContactService contactService;
    private final SocialMediaService socialMediaService;
    private final MediaService mediaService;
    private final CharacteristicsService characteristicsService;
    private final TalentProfileService talentProfileService;
    private final CreditService creditService;
    private final EducationService educationService;
    private final AdminProfileMapper adminProfileMapper;
    private final HistoryService historyService;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    @Override
    @Transactional
    public AdminTalentProfileResponse updateTalentProfile(UUID userId, AdminTalentProfileUpdateRequest request) {
        if (!request.hasChanges()) {
            throw ApiException.badRequest(ADMIN_USER_UPDATE_NO_CHANGES);
        }

        userRepository.findByIdIncludingDeleted(userId)
            .filter(user -> !PROPOSALS_SYSTEM_USER_ID.equals(user.getId()))
            .orElseThrow(() -> ApiException.notFound(PROFILE_NOT_FOUND));
        var profile = talentProfileRepository.findTalentProfileForAdminByUserId(userId)
            .orElseThrow(() -> ApiException.notFound(PROFILE_NOT_FOUND));
        var profileId = profile.getId();
        if (request.media() != null) {
            var supabase = appProperties.getSupabase();
            TalentMediaUrlPolicy.requireManagedPhotoUploads(
                request.media(),
                supabase.getUrl(),
                supabase.getMediaBucket(),
                profileId
            );
        }
        // Serialized before any change and before the persistence context is cleared: the response
        // holds lazy collections that cannot be read once their entities are detached.
        JsonNode before = objectMapper.valueToTree(adminProfileMapper.toTalentProfileResponse(profile));

        if (request.basicInfo() != null) basicInfoService.patchBasicInfo(profile, request.basicInfo());
        if (request.contact() != null) contactService.patchContact(profile, request.contact());
        if (request.socialMedia() != null) socialMediaService.patchSocialMedia(profile, request.socialMedia());
        if (request.media() != null) mediaService.patchMedia(profile, request.media());
        if (request.characteristics() != null) characteristicsService.patchCharacteristics(profile, request.characteristics());
        if (request.skills() != null) talentProfileService.patchSkills(profile, request.skills());
        if (request.credits() != null) creditService.replaceCredits(profile, request.credits());
        if (request.education() != null) educationService.replaceEducation(profile, request.education());

        entityManager.flush();
        entityManager.clear();

        var refreshed = talentProfileRepository.findTalentProfileForAdminByUserId(userId)
            .orElseThrow(() -> ApiException.notFound(PROFILE_NOT_FOUND));
        var after = adminProfileMapper.toTalentProfileResponse(refreshed);
        JsonNode afterTree = objectMapper.valueToTree(after);

        var changes = new ArrayList<HistoryChangeEntry>();
        if (request.basicInfo() != null) changes.addAll(diff("basicInfo", before.get("basicInfo"), afterTree.get("basicInfo")));
        if (request.contact() != null) changes.addAll(diff("contact", before.get("contact"), afterTree.get("contact")));
        if (request.socialMedia() != null) changes.addAll(diff("socialMedia", before.get("socialMedia"), afterTree.get("socialMedia")));
        if (request.media() != null) changes.addAll(diff("media", before.get("media"), afterTree.get("media")));
        if (request.characteristics() != null) changes.addAll(diff("characteristics", before.get("characteristics"), afterTree.get("characteristics")));
        if (request.skills() != null) changes.addAll(diff("skills", before.get("skills"), afterTree.get("skills")));
        if (request.credits() != null) changes.addAll(diff("credits", before.get("credits"), afterTree.get("credits")));
        if (request.education() != null) changes.addAll(diff("education", before.get("education"), afterTree.get("education")));

        if (!changes.isEmpty()) {
            historyService.createHistoryEntry(EntityType.TALENT_PROFILE, profileId, request.reason(), changes);
        }

        return after;
    }

    private List<HistoryChangeEntry> diff(String section, JsonNode before, JsonNode after) {
        return ProfileChangeDiff.diff(objectMapper, section, before, after);
    }
}
