package com.padimasso.autocasting.service.impl;

import com.padimasso.autocasting.auth.model.UserEntity;
import com.padimasso.autocasting.auth.service.AuthContext;
import com.padimasso.autocasting.dto.ProfileResponse;
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

}
