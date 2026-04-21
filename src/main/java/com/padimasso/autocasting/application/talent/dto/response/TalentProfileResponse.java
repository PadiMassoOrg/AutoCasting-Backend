package com.padimasso.autocasting.application.talent.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record TalentProfileResponse(
    UUID id,
    String userAccountProvider,
    String planStringCode,
    String publicSlug,
    BasicInfoResponse basicInfo,
    ContactResponse contact,
    SocialMediaResponse socialMedia,
    MediaResponse media,
    CharacteristicsResponse characteristics,
    Set<SiteMetadataObject> skills,
    Set<CreditResponse> credits,
    Set<EducationResponse> education,
    LocalDateTime modifiedAt
) {
}
