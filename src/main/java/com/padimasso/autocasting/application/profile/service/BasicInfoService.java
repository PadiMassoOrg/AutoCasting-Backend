package com.padimasso.autocasting.application.profile.service;

import com.padimasso.autocasting.application.profile.dto.request.BasicInfoPatchRequest;
import com.padimasso.autocasting.application.profile.dto.response.BasicInfoResponse;

public interface BasicInfoService {
    BasicInfoResponse patchMyBasicInfo(BasicInfoPatchRequest req);
}
