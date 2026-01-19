package com.padimasso.autocasting.application.castings.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.UUID;

public record CastingRoleCharacteristicsResponse(
    UUID id,
    Integer heightCm,
    SiteMetadataObject ethnicity,
    Integer weightKg,
    SiteMetadataObject hairColor,
    SiteMetadataObject eyeColor,
    String chestCm,
    String waistCm,
    String hipCm,
    String shirtSize,
    String pantSize,
    String dressSize,
    String shoeSize,
    boolean tattoo,
    boolean passport,
    boolean drivingLicense,
    SiteMetadataObject dietOption
) {
}
