package com.padimasso.autocasting.application.admin.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.padimasso.autocasting.application.admin.dto.request.AdminEmployerProfileUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminEmployerProfileResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminProfileMapper;
import com.padimasso.autocasting.application.admin.service.AdminEmployerProfileEditService;
import com.padimasso.autocasting.application.admin.util.ProfileChangeDiff;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.employer.service.EmployerBasicInfoService;
import com.padimasso.autocasting.application.employer.util.EmployerLogoUrlPolicy;
import com.padimasso.autocasting.application.history.dto.HistoryChangeEntry;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.application.talent.service.SocialMediaService;
import com.padimasso.autocasting.config.AppProperties;
import com.padimasso.autocasting.exception.ApiException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.PROPOSALS_SYSTEM_USER_ID;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.ADMIN_USER_UPDATE_NO_CHANGES;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AdminEmployerProfileEditServiceImpl implements AdminEmployerProfileEditService {

    private final UserRepository userRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final EmployerBasicInfoService employerBasicInfoService;
    private final SocialMediaService socialMediaService;
    private final AdminProfileMapper adminProfileMapper;
    private final HistoryService historyService;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    @Override
    @Transactional
    public AdminEmployerProfileResponse updateEmployerProfile(UUID userId, AdminEmployerProfileUpdateRequest request) {
        if (!request.hasChanges()) {
            throw ApiException.badRequest(ADMIN_USER_UPDATE_NO_CHANGES);
        }

        userRepository.findByIdIncludingDeleted(userId)
            .filter(user -> !PROPOSALS_SYSTEM_USER_ID.equals(user.getId()))
            .orElseThrow(() -> ApiException.notFound(PROFILE_NOT_FOUND));
        var profile = employerProfileRepository.findEmployerProfileForAdminByUserId(userId)
            .orElseThrow(() -> ApiException.notFound(PROFILE_NOT_FOUND));
        var profileId = profile.getId();

        if (request.basicInfo() != null) {
            var supabase = appProperties.getSupabase();
            EmployerLogoUrlPolicy.requireManagedLogoUpload(request.basicInfo(), supabase.getUrl(), supabase.getMediaBucket(), profileId);
        }
        if (request.socialMedia() != null && profile.getBasicInfo() == null) {
            throw ApiException.notFound(PROFILE_NOT_FOUND);
        }

        // Serialized before any change and before the persistence context is cleared: the response
        // may hold lazy collections that cannot be read once their entities are detached.
        JsonNode before = objectMapper.valueToTree(adminProfileMapper.toEmployerProfileResponse(profile));

        if (request.basicInfo() != null) employerBasicInfoService.patchBasicInfo(profile, request.basicInfo());
        if (request.socialMedia() != null) socialMediaService.patchEmployerSocialMedia(profile, request.socialMedia());

        entityManager.flush();
        entityManager.clear();

        var refreshed = employerProfileRepository.findEmployerProfileForAdminByUserId(userId)
            .orElseThrow(() -> ApiException.notFound(PROFILE_NOT_FOUND));
        var after = adminProfileMapper.toEmployerProfileResponse(refreshed);
        JsonNode afterTree = objectMapper.valueToTree(after);

        var changes = new ArrayList<HistoryChangeEntry>();
        if (request.basicInfo() != null) {
            changes.addAll(ProfileChangeDiff.diff(objectMapper, "basicInfo", withoutSocialMedia(before), withoutSocialMedia(afterTree)));
        }
        if (request.socialMedia() != null) {
            changes.addAll(ProfileChangeDiff.diff(objectMapper, "socialMedia", socialMedia(before), socialMedia(afterTree)));
        }

        if (!changes.isEmpty()) {
            historyService.createHistoryEntry(EntityType.EMPLOYER_PROFILE, profileId, request.reason(), changes);
        }

        return after;
    }

    private static JsonNode basicInfo(JsonNode profile) {
        return profile == null ? null : profile.get("basicInfo");
    }

    private static JsonNode socialMedia(JsonNode profile) {
        JsonNode basicInfo = basicInfo(profile);
        return basicInfo == null ? null : basicInfo.get("socialMedia");
    }

    private static JsonNode withoutSocialMedia(JsonNode profile) {
        JsonNode basicInfo = basicInfo(profile);
        if (basicInfo == null || !basicInfo.isObject()) return basicInfo;
        var copy = basicInfo.deepCopy();
        ((com.fasterxml.jackson.databind.node.ObjectNode) copy).remove("socialMedia");
        return copy;
    }
}
