package com.padimasso.autocasting.application.employer.service;

import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.dto.request.EmployerBasicInfoPatchRequest;
import com.padimasso.autocasting.application.employer.dto.response.EmployerBasicInfoResponse;

public interface EmployerBasicInfoService {
    EmployerBasicInfoResponse patchMyBasicInfo(EmployerBasicInfoPatchRequest req);

    EmployerBasicInfoResponse patchBasicInfo(EmployerProfileEntity profile, EmployerBasicInfoPatchRequest req);
}
