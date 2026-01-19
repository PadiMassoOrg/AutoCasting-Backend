package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.talent.dto.response.SocialMediaResponse;

import java.time.LocalDate;
import java.util.UUID;

public record CastingEmployerInfoResponse(
    UUID id,
    String companyName,
    SiteMetadataObject companyType,
    String imageUrl,
    SocialMediaResponse socialMedia,
    Long totalCastings,
    LocalDate memberSince
) {
}
