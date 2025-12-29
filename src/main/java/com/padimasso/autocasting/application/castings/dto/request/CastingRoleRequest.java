package com.padimasso.autocasting.application.castings.dto.request;

import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;
import java.util.UUID;

public record CastingRoleRequest(
    UUID rolesSectionId,
    String roleName,
    UUID roleTypeId,
    UUID genderId,
    Short ageMin,
    Short ageMax,
    JsonNullable<String> description,
    Set<UUID> professionIds,
    JsonNullable<CastingRoleCharacteristicsRequest> characteristics,
    JsonNullable<Set<UUID>> skillIds
) {
}
