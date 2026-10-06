package com.padimasso.autocasting.application.talent.service;

import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.dto.request.BasicInfoPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.BasicInfoResponse;

public interface BasicInfoService {
    BasicInfoResponse patchMyBasicInfo(BasicInfoPatchRequest req);

    BasicInfoResponse patchBasicInfo(TalentProfileEntity profile, BasicInfoPatchRequest req);
}
