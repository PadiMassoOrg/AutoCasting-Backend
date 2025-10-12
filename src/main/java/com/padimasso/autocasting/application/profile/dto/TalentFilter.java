package com.padimasso.autocasting.application.profile.dto;

import com.padimasso.autocasting.application.common.dto.MatchMode;

import java.util.List;
import java.util.UUID;

public record TalentFilter(
    String stageName,                 // contains, case-insensitive
    Integer ageMin,                   // (incl.)
    Integer ageMax,                   // (incl.)
    List<String> genderIdTokens,
    List<UUID> professionIds,
    MatchMode professionsMode,        // ANY (default) | ALL
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
    MatchMode skillsMode,             // ANY (default) | ALL
    Boolean includeNoHeadshot
) {}
