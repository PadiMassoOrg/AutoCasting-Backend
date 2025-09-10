package com.padimasso.autocasting.application.profile.dto;

import com.padimasso.autocasting.application.common.dto.MatchMode;

import java.util.List;
import java.util.UUID;

public record TalentFilter(
    String stageName,                 // contains, case-insensitive
    Integer ageMin,                   // años (incl.)
    Integer ageMax,                   // años (incl.)
    UUID genderId,
    List<UUID> professionIds,       // SiteMetadata ids
    MatchMode professionsMode,        // ANY (default) | ALL
    Integer heightMinCm,
    Integer heightMaxCm,
    UUID hairColorId,
    UUID eyeColorId,
    Boolean tattoo,
    Boolean passport,
    Boolean drivingLicense,
    List<UUID> skillIds,            // SiteMetadata ids
    MatchMode skillsMode              // ANY (default) | ALL
) {}
