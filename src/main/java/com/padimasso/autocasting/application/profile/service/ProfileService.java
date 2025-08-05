package com.padimasso.autocasting.application.profile.service;

import com.padimasso.autocasting.application.profile.dto.response.ProfileResponse;
import com.padimasso.autocasting.application.profile.dto.response.PublicProfileResponse;

public interface ProfileService {

    ProfileResponse getMyProfile();

    PublicProfileResponse getProfileBySlug(String slug);
}
