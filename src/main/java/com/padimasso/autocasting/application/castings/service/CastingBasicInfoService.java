package com.padimasso.autocasting.application.castings.service;

import com.padimasso.autocasting.application.castings.dto.request.CastingBasicInfoPatchRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingBasicInfoResponse;
import jakarta.validation.Valid;

public interface CastingBasicInfoService {
    CastingBasicInfoResponse patchCastingBasicInfo(@Valid CastingBasicInfoPatchRequest request);
}
