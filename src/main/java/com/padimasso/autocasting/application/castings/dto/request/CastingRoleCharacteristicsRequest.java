package com.padimasso.autocasting.application.castings.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.UUID;

public record CastingRoleCharacteristicsRequest(
    UUID roleId,
    @Min(value = 0, message = "talent.height_min")
    @Max(value = 300, message = "talent.height_max")
    JsonNullable<Integer> heightCm,
    UUID ethnicityId,
    @Min(value = 0, message = "talent.weight_min")
    @Max(value = 500, message = "talent.weight_max")
    JsonNullable<Integer> weightKg,
    UUID hairColorId,
    UUID eyeColorId,
    @Size(max = 255, message = "talent.chest_max_length")
    JsonNullable<String> chestCm,
    @Size(max = 255, message = "talent.waist_max_length")
    JsonNullable<String> waistCm,
    @Size(max = 255, message = "talent.hip_max_length")
    JsonNullable<String> hipCm,
    @Size(max = 255, message = "talent.shirt_size_max_length")
    JsonNullable<String> shirtSize,
    @Size(max = 255, message = "talent.pant_size_max_length")
    JsonNullable<String> pantSize,
    @Size(max = 255, message = "talent.dress_size_max_length")
    JsonNullable<String> dressSize,
    @Size(max = 255, message = "talent.shoe_size_max_length")
    JsonNullable<String> shoeSize,
    JsonNullable<Boolean> tattoo,
    JsonNullable<Boolean> passport,
    JsonNullable<Boolean> drivingLicense,
    UUID dietOptionId
) {
}
