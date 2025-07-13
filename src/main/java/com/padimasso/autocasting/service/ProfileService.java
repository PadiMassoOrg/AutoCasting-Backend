package com.padimasso.autocasting.service;

import com.padimasso.autocasting.dto.request.PublicProfileRequest;
import com.padimasso.autocasting.dto.response.ProfileResponse;
import com.padimasso.autocasting.dto.response.PublicProfileResponse;

public interface ProfileService {

    ProfileResponse getMyProfile();

    PublicProfileResponse getProfileBySlug(PublicProfileRequest publicProfileRequest);
}
