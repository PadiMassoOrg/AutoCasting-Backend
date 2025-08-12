package com.padimasso.autocasting.application.profile.service;

import com.padimasso.autocasting.application.profile.dto.request.EducationRequest;
import com.padimasso.autocasting.application.profile.dto.response.EducationResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface EducationService {
    EducationResponse createEducation(EducationRequest request);

    List<EducationResponse> listMyEducation();

    EducationResponse getMyEducation(UUID id);

    EducationResponse patchMyEducation(UUID id, @Valid EducationRequest request);

    void deleteMyEducation(UUID id);
}
