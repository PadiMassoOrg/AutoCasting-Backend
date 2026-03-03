package com.padimasso.autocasting.application.applications.service;

import com.padimasso.autocasting.application.applications.dto.EmployerCastingApplicantsFilter;
import com.padimasso.autocasting.application.applications.dto.TalentCastingApplicationsFilter;
import com.padimasso.autocasting.application.applications.dto.request.CastingApplicationRequest;
import com.padimasso.autocasting.application.applications.dto.response.EmployerCastingApplicantCardResponse;
import com.padimasso.autocasting.application.applications.dto.response.TalentCastingApplicationCardResponse;
import com.padimasso.autocasting.application.shared.web.SliceResponse;

import java.util.UUID;

public interface CastingApplicationService {
    void apply(UUID roleId, CastingApplicationRequest request);

    SliceResponse<EmployerCastingApplicantCardResponse> getEmployerCastingApplicants(EmployerCastingApplicantsFilter filter, int page, int size);

    SliceResponse<TalentCastingApplicationCardResponse> getTalentCastingApplications(TalentCastingApplicationsFilter filter, int page, int size);
}
