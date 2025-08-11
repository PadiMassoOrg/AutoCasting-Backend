package com.padimasso.autocasting.application.profile.service;

import com.padimasso.autocasting.application.profile.dto.request.SkillsPatchRequest;
import com.padimasso.autocasting.application.profile.dto.response.ProfileResponse;
import com.padimasso.autocasting.application.profile.dto.response.PublicProfileResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import jakarta.validation.Valid;

import java.util.Set;

public interface ProfileService {

    ProfileResponse getMyProfile();

    PublicProfileResponse getProfileBySlug(String slug);

    Set<SiteMetadataObject> patchMySkills(@Valid SkillsPatchRequest request);
}
