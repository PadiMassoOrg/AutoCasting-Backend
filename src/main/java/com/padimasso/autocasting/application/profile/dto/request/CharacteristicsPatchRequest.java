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
    boolean tattoo,
    boolean passport,
    boolean drivingLicense,
    UUID dietOptionId
) {
}
