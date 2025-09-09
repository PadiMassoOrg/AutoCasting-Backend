package com.padimasso.autocasting.application.profile.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.List;
import java.util.UUID;

public record ProfileCardResponse(
    UUID id,
    String publicSlug,
    String stageName,
    String email,
    String phoneNumber,
    String headshotImageUrl,
    List<SiteMetadataObject> professions
) {}
