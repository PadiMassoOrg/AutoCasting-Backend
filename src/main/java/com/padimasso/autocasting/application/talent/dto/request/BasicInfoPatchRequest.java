package com.padimasso.autocasting.application.talent.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import com.padimasso.autocasting.application.talent.TalentProfessionConstraints;

public record BasicInfoPatchRequest(
    @Size(max = 255, message = "talent.stage_name_max_length")
    String stageName,
    UUID genderId,
    @Past(message = "validation.birth_date_future")
    LocalDate birthDate,
    @Size(max = TalentProfessionConstraints.MAX_PROFESSIONS, message = "talent.professions_max")
    Set<@NotNull(message = "talent.professions_required") UUID> professionIds
) {
}
