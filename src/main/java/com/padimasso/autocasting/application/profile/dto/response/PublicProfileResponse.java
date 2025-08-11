package com.padimasso.autocasting.application.profile.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.List;
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
    List<SiteMetadataObject> skills,
    List<CreditResponse> credits,
    List<EducationResponse> education
) {
}
