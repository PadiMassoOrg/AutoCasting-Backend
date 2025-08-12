package com.padimasso.autocasting.application.profile.service;

import com.padimasso.autocasting.application.profile.dto.request.EducationRequest;
import com.padimasso.autocasting.application.profile.dto.response.EducationResponse;

public interface EducationService {
    EducationResponse createEducation(EducationRequest request);
}
