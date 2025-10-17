package com.padimasso.autocasting.application.talent.dto.request;

import org.openapitools.jackson.nullable.JsonNullable;

import java.util.UUID;

public record CharacteristicsPatchRequest(
    JsonNullable<Integer> heightCm,
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
    Boolean tattoo,
    Boolean passport,
    Boolean drivingLicense,
    UUID dietOptionId
) {
}
