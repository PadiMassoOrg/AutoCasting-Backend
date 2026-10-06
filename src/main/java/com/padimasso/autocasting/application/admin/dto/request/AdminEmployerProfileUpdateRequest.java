package com.padimasso.autocasting.application.admin.dto.request;

import com.padimasso.autocasting.application.employer.dto.request.EmployerBasicInfoPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.SocialMediaPatchRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/** Every section is optional: a null section is left untouched. */
public record AdminEmployerProfileUpdateRequest(
    @NotBlank(message = "validation.required")
    String reason,
    @Valid EmployerBasicInfoPatchRequest basicInfo,
    @Valid SocialMediaPatchRequest socialMedia
) {
    public boolean hasChanges() {
        return basicInfo != null || socialMedia != null;
    }
}
