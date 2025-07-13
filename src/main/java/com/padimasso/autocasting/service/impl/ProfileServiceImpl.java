package com.padimasso.autocasting.service.impl;

import com.padimasso.autocasting.auth.model.UserEntity;
import com.padimasso.autocasting.auth.repository.UserRepository;
import com.padimasso.autocasting.auth.service.AuthContext;
import com.padimasso.autocasting.dto.response.ProfileResponse;
import com.padimasso.autocasting.dto.response.PublicProfileResponse;
import com.padimasso.autocasting.model.ProfileEntity;
import com.padimasso.autocasting.repository.ProfileRepository;
import com.padimasso.autocasting.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ProfileServiceImpl implements ProfileService {

    private final AuthContext authContext;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Override
    public ProfileResponse getMyProfile() {
        UserEntity user = authContext.getCurrentUserOrThrow();

        var foundProfile = profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));

        return new ProfileResponse(
            user.getEmail(),
            user.getRole().getNameStringCode(),
            foundProfile.getPlan().getNameStringCode(),
            foundProfile.getName(),
            foundProfile.getPublicSlug()
        );
    }

    @Override
    public PublicProfileResponse getProfileBySlug(String slug) {

        ProfileEntity foundProfile = profileRepository
            .findByDefaultSlugOrPremiumSlug(slug, slug)
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));

        return new PublicProfileResponse(
            foundProfile.getUser().getEmail(),
            foundProfile.getUser().getRole().getNameStringCode(),
            foundProfile.getPlan().getNameStringCode(),
            foundProfile.getName(),
            foundProfile.getPublicSlug()
        );
    }

}
