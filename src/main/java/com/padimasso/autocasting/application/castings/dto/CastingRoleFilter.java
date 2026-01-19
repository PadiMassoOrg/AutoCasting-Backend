package com.padimasso.autocasting.application.castings.dto;

import com.padimasso.autocasting.application.common.dto.MatchMode;

import java.util.List;
import java.util.UUID;

public record CastingRoleFilter(
    String roleName,
    Integer ageMin,
    Integer ageMax,
    List<String> genderIdTokens,
    List<String> ethnicityIdTokens,
    List<UUID> professionIds,
    MatchMode professionsMode,
    Integer heightMinCm,
    Integer heightMaxCm,
    List<UUID> hairColorIds,
    MatchMode hairColorIdsMode,
    List<UUID> eyeColorIds,
    MatchMode eyeColorIdsMode,
    Boolean tattoo,
    Boolean passport,
    Boolean drivingLicense,
    List<UUID> skillIds,
    MatchMode skillsMode,
    List<UUID> projectTypeIds,
    List<UUID> castingModalityIds,
    String locationText
) {
}
