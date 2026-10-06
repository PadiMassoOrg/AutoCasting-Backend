package com.padimasso.autocasting.application.talent.service;

import com.padimasso.autocasting.application.common.dto.LastModifiedResponse;
import com.padimasso.autocasting.application.talent.dto.request.EducationRequest;
import com.padimasso.autocasting.application.talent.dto.request.EducationUpsertRequest;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.dto.response.EducationResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface EducationService {
    EducationResponse createEducation(EducationRequest request);

    List<EducationResponse> listMyEducation();

    EducationResponse getMyEducation(UUID id);

    EducationResponse patchMyEducation(UUID id, @Valid EducationRequest request);

    LastModifiedResponse deleteMyEducation(UUID id);

    List<EducationResponse> replaceEducation(TalentProfileEntity profile, List<EducationUpsertRequest> items);
}
