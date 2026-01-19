package com.padimasso.autocasting.application.castings.dto.request;

import org.openapitools.jackson.nullable.JsonNullable;

import java.util.UUID;

public record CastingRoleCharacteristicsRequest(
    UUID roleId,
    JsonNullable<Integer> heightCm,
    UUID ethnicityId,
    JsonNullable<Integer> weightKg,
    UUID hairColorId,
    UUID eyeColorId,
    JsonNullable<String> chestCm,
    JsonNullable<String> waistCm,
    JsonNullable<String> hipCm,
    JsonNullable<String> shirtSize,
    JsonNullable<String> pantSize,
    JsonNullable<String> dressSize,
    JsonNullable<String> shoeSize,
    JsonNullable<Boolean> tattoo,
    JsonNullable<Boolean> passport,
    JsonNullable<Boolean> drivingLicense,
    UUID dietOptionId
) {
}
