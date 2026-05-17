package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.time.LocalDate;
import java.util.UUID;

public record PublicCastingEmployerInfoResponse(
    UUID id,
    String companyName,
    SiteMetadataObject companyType,
    String imageUrl,
    PublicSocialMediaResponse socialMedia,
    Long totalCastings,
    LocalDate memberSince,
    String websiteUrl
) {
}
