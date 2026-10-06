package com.padimasso.autocasting.application.admin.dto.request;

import com.padimasso.autocasting.application.talent.dto.request.BasicInfoPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.CharacteristicsPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.ContactPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.CreditUpsertRequest;
import com.padimasso.autocasting.application.talent.dto.request.EducationUpsertRequest;
import com.padimasso.autocasting.application.talent.dto.request.MediaPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.SkillsPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.SocialMediaPatchRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Every section is optional: a null section is left untouched. {@code credits} and {@code education}
 * replace the whole list (items without an id are created, existing items missing from the list are deleted).
 */
public record AdminTalentProfileUpdateRequest(
    @NotBlank(message = "validation.required")
    String reason,
    @Valid BasicInfoPatchRequest basicInfo,
    @Valid ContactPatchRequest contact,
    @Valid SocialMediaPatchRequest socialMedia,
    @Valid MediaPatchRequest media,
    @Valid CharacteristicsPatchRequest characteristics,
    @Valid SkillsPatchRequest skills,
    @Valid List<CreditUpsertRequest> credits,
    @Valid List<EducationUpsertRequest> education
) {
    public boolean hasChanges() {
        return basicInfo != null
            || contact != null
            || socialMedia != null
            || media != null
            || characteristics != null
            || skills != null
            || credits != null
            || education != null;
    }
}
