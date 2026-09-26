package com.padimasso.autocasting.application.proposal.type.casting.dto.request;

import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record CastingProposalRoleRequest(
    UUID id,
    @NotBlank(message = "casting.role_name_required")
    @Size(max = 255, message = "casting.role_name_max_length")
    String roleName,
    @NotNull(message = "casting.role_type_required")
    UUID roleTypeId,
    @NotNull(message = "casting.gender_required")
    UUID genderId,
    @NotNull(message = "casting.age_min_required")
    @Min(value = 0, message = "casting.age_min")
    @Max(value = 99, message = "casting.age_max")
    Short ageMin,
    @NotNull(message = "casting.age_max_required")
    @Min(value = 0, message = "casting.age_min")
    @Max(value = 99, message = "casting.age_max")
    Short ageMax,
    @Size(max = 2000, message = "casting.description_max_length")
    String description,
    @NotEmpty(message = "talent.professions_required")
    Set<@NotNull(message = "talent.professions_required") UUID> professionIds,
    Set<@NotNull(message = "talent.skills_required") UUID> skillIds,
    @NotNull(message = "casting.pay_rate_type_required")
    UUID payRateTypeId,
    UUID currencyId,
    BigDecimal amount,
    @Size(max = 2000, message = "casting.remuneration_notes_max_length")
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

    public CastingRoleRequest toCastingRoleRequest() {
        return new CastingRoleRequest(
            null, roleName, roleTypeId, genderId, ageMin, ageMax, description, professionIds, skillIds,
            payRateTypeId, currencyId, amount, remunerationNotes, requiresAudio, requiresVideo,
            requirementDescription, ethnicityId, tattoo, passport, drivingLicense, null
        );
    }
}
