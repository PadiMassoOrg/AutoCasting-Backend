package com.padimasso.autocasting.application.castings.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record CastingRoleRequest(
    @NotNull(message = "casting.id_required")
    UUID castingId,
    @NotBlank(message = "casting.role_name_required")
    @Size(max = 255, message = "casting.role_name_max_length")
    String roleName,
    @NotNull(message = "casting.role_type_required")
    UUID roleTypeId,
    @NotNull(message = "casting.gender_required")
    UUID genderId,
    @NotNull(message = "casting.age_min_required")
    @Min(value = 0, message = "casting.age_min")
    @Max(value = 150, message = "casting.age_max")
    Short ageMin,
    @NotNull(message = "casting.age_max_required")
    @Min(value = 0, message = "casting.age_min")
    @Max(value = 150, message = "casting.age_max")
    Short ageMax,
    String description,
    Set<@NotNull(message = "talent.professions_required") UUID> professionIds,
    Set<@NotNull(message = "talent.skills_required") UUID> skillIds,
    @NotNull(message = "casting.pay_rate_type_required")
    UUID payRateTypeId,
    UUID currencyId,
    BigDecimal amount,
    String remunerationNotes,
    Boolean requiresAudio,
    Boolean requiresVideo,
    @Size(max = 3000, message = "casting.requirement_description_max_length")
    String requirementDescription,
    UUID ethnicityId,
    Boolean tattoo,
    Boolean passport,
    Boolean drivingLicense
) {
    @AssertTrue(message = "casting.age_range_invalid")
    public boolean isAgeRangeValid() {
        return ageMin == null || ageMax == null || ageMin <= ageMax;
    }
}
