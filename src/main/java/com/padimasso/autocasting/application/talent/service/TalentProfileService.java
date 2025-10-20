package com.padimasso.autocasting.application.talent.service;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.talent.dto.request.SkillsPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.PublicProfileResponse;
import com.padimasso.autocasting.application.talent.dto.response.TalentProfileResponse;
import jakarta.validation.Valid;

import java.util.Set;

public interface TalentProfileService {

    TalentProfileResponse getMyProfile();

    PublicProfileResponse getProfileBySlug(String slug);

    Set<SiteMetadataObject> patchMySkills(@Valid SkillsPatchRequest request);
}
