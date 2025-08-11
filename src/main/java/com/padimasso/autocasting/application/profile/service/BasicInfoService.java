package com.padimasso.autocasting.application.profile.service;

import com.padimasso.autocasting.application.profile.dto.request.BasicInfoPatchRequest;
import com.padimasso.autocasting.application.profile.dto.response.BasicInfoResponse;
import jakarta.transaction.Transactional;

public interface BasicInfoService {
    @Transactional
    BasicInfoResponse patchMyBasicInfo(BasicInfoPatchRequest req);
}
