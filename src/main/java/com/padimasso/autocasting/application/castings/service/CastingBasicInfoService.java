package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.request.section.CastingBasicInfoPatchRequest;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingBasicInfoResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface CastingBasicInfoService {
    CastingBasicInfoResponse patchCastingBasicInfo(@Valid CastingBasicInfoPatchRequest request);

    CastingBasicInfoResponse getBySectionId(UUID sectionId);
}
