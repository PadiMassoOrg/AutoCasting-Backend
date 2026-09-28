package com.padimasso.autocasting.application.admin.dto.response;

import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.talent.dto.response.BasicInfoResponse;
import com.padimasso.autocasting.application.talent.dto.response.CharacteristicsResponse;
import com.padimasso.autocasting.application.talent.dto.response.ContactResponse;
import com.padimasso.autocasting.application.talent.dto.response.CreditResponse;
import com.padimasso.autocasting.application.talent.dto.response.EducationResponse;
import com.padimasso.autocasting.application.talent.dto.response.MediaResponse;
import com.padimasso.autocasting.application.talent.dto.response.SocialMediaResponse;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record AdminTalentProfileResponse(
    UUID id,
    String publicSlug,
    boolean visibleInCatalog,
    OnboardingStatus onboardingStatus,
    BasicInfoResponse basicInfo,
    ContactResponse contact,
    SocialMediaResponse socialMedia,
    MediaResponse media,
    CharacteristicsResponse characteristics,
    Set<SiteMetadataObject> skills,
    Set<CreditResponse> credits,
    Set<EducationResponse> education,
    LocalDateTime lastSavedAt,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {
}
