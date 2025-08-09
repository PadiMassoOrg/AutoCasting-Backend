package com.padimasso.autocasting.application.profile.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.profile.dto.response.ProfileResponse;
import com.padimasso.autocasting.application.profile.dto.response.PublicProfileResponse;
import com.padimasso.autocasting.application.profile.mapper.ProfileMapper;
import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
import com.padimasso.autocasting.application.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ProfileServiceImpl implements ProfileService {

    private final AuthContext authContext;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

    @Override
    public ProfileResponse getMyProfile() {
        UserEntity user = authContext.getCurrentUserOrThrow();

        var foundProfile = profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));

        return profileMapper.toProfileResponse(foundProfile, user);
    }

    @Override
    public PublicProfileResponse getProfileBySlug(String slug) {

        ProfileEntity foundProfile = profileRepository
            .findByDefaultSlugOrPremiumSlug(slug, slug)
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));

        return profileMapper.toPublicProfileResponse(foundProfile);
    }

}
