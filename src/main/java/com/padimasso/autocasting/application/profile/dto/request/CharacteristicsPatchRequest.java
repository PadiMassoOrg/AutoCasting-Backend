package com.padimasso.autocasting.application.profile.dto.request;

import java.util.UUID;

public record CharacteristicsPatchRequest(
    Integer heightCm,
    Integer weightKg,
    UUID hairColorId,
    UUID eyeColorId,
    Integer chestCm,
    Integer waistCm,
    Integer hipCm,
    String shirtSize,
    String pantSize,
    String dressSize,
    String shoeSize,
    Boolean tattoo,
    Boolean passport,
    Boolean drivingLicense,
    UUID dietOptionId
) {
}
