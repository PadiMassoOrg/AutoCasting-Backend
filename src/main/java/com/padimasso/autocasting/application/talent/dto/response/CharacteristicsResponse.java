package com.padimasso.autocasting.application.talent.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.UUID;

public record CharacteristicsResponse(
    UUID id,
    Integer heightCm,
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
