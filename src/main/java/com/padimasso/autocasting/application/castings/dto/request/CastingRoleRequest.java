package com.padimasso.autocasting.application.castings.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;
import java.util.UUID;

public record CastingRoleRequest(
    @NotNull(message = "casting.roles_section_required")
    UUID rolesSectionId,
    @NotNull(message = "casting.role_name_required")
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
    JsonNullable<String> description,
    Set<@NotNull(message = "talent.professions_required") UUID> professionIds,
    @Valid
    JsonNullable<CastingRoleCharacteristicsRequest> characteristics,
    JsonNullable<Set<@NotNull(message = "talent.skills_required") UUID>> skillIds
) {
}
