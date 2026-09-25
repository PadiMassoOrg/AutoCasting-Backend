package com.padimasso.autocasting.application.proposal.type.casting.dto.request;

import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

// Same fields and rules as CastingRoleRequest, minus castingId: the casting doesn't exist yet,
// it's created in the same request. No referencePhotoUrl either (photos aren't supported in v1).
public record CastingProposalRoleRequest(
    @NotBlank(message = "casting.role_name_required")
    @Size(max = 255, message = "casting.role_name_max_length")
    String roleName,
    @NotNull(message = "casting.role_type_required")
    UUID roleTypeId,
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

    public CastingRoleRequest toCastingRoleRequest() {
        return new CastingRoleRequest(
            null, roleName, roleTypeId, genderId, ageMin, ageMax, description, professionIds, skillIds,
            payRateTypeId, currencyId, amount, remunerationNotes, requiresAudio, requiresVideo,
            requirementDescription, ethnicityId, tattoo, passport, drivingLicense, null
        );
    }
}
