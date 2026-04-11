package com.padimasso.autocasting.application.talent.service;

import com.padimasso.autocasting.application.talent.dto.request.SkillsPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.PublicProfileResponse;
import com.padimasso.autocasting.application.talent.dto.response.SkillsResponse;
import com.padimasso.autocasting.application.talent.dto.response.TalentProfileResponse;
import jakarta.validation.Valid;

public interface TalentProfileService {

    TalentProfileResponse getMyProfile();

    PublicProfileResponse getProfileBySlug(String slug);

    SkillsResponse patchMySkills(@Valid SkillsPatchRequest request);
}
