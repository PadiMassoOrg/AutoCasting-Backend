package com.padimasso.autocasting.application.profile.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.List;
import java.util.UUID;

public record ProfileResponse(
    UUID id,
    String roleStringCode,
    String planStringCode,
    String publicSlug,
    BasicInfoResponse basicInfo,
    ContactResponse contact,
    SocialMediaResponse socialMedia,
    CharacteristicsResponse characteristics,
    List<SiteMetadataObject> skills,
    List<SiteMetadataObject> professions,
    List<CreditResponse> credits
) {
}
