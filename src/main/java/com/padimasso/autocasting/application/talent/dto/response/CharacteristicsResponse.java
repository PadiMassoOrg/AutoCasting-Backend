package com.padimasso.autocasting.application.talent.dto.response;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;

import java.util.UUID;

public record CharacteristicsResponse(
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
    Boolean tattoo,
    Boolean passport,
    Boolean drivingLicense,
    SiteMetadataObject dietOption
) {
}
