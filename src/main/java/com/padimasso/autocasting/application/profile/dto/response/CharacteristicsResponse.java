package com.padimasso.autocasting.application.profile.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.UUID;

public record CharacteristicsResponse(
    UUID id,
    Integer heightCm,
    Integer weightKg,
    SiteMetadataObject hairColor,
    SiteMetadataObject eyeColor,
    Integer chestCm,
    Integer waistCm,
    Integer hipCm,
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
