package com.padimasso.autocasting.application.talent.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.Set;
import java.util.UUID;

public record PublicProfileResponse(
    UUID id,
    String roleStringCode,
    String planStringCode,
    String publicSlug,
    BasicInfoResponse basicInfo,
    ContactResponse contact,
    SocialMediaResponse socialMedia,
    MediaResponse media,
    CharacteristicsResponse characteristics,
    Set<SiteMetadataObject> skills,
    Set<CreditResponse> credits,
    Set<EducationResponse> education
) {
}
