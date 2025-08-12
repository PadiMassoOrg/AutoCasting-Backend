package com.padimasso.autocasting.application.profile.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.profile.dto.request.SkillsPatchRequest;
import com.padimasso.autocasting.application.profile.dto.response.ProfileResponse;
import com.padimasso.autocasting.application.profile.dto.response.PublicProfileResponse;
import com.padimasso.autocasting.application.profile.mapper.ProfileMapper;
import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import com.padimasso.autocasting.application.profile.repository.CreditRepository;
import com.padimasso.autocasting.application.profile.repository.EducationRepository;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
import com.padimasso.autocasting.application.profile.service.ProfileService;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ProfileServiceImpl implements ProfileService {

    private final AuthContext authContext;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private static final String PROFILE_NOT_FOUND = "profile.not_found";
    private final CreditRepository creditRepository;
    private final ProfileMapper profileMapper;
    private final EducationRepository educationRepository;

    @Override
    public ProfileResponse getMyProfile() {
        UserEntity user = authContext.getCurrentUserOrThrow();

        var foundProfile = profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));

        return profileMapper.toProfileResponse(foundProfile, user);
    }

    @Override
    public PublicProfileResponse getProfileBySlug(String slug) {

        ProfileEntity foundProfile = profileRepository
            .findByDefaultSlugOrPremiumSlug(slug, slug)
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));

        return profileMapper.toPublicProfileResponse(foundProfile);
    }

    @Override
    public Set<SiteMetadataObject> patchMySkills(SkillsPatchRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        var foundProfile = profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));

        if (request.skillIds() != null) {
            Set<UUID> ids = request.skillIds();
            if (ids.isEmpty()) {
                foundProfile.getSkills().clear();
            } else {
                var found = new HashSet<>(skillRepository.findAllByIdIn(ids));
                if (found.size() != ids.size()) {
                    throw new IllegalArgumentException("sitemetadata.skill.invalid_ids");
                }
                foundProfile.setSkills(found);
            }
        }

        return profileMapper.mapToSiteMetadataObjectList(profileRepository.save(foundProfile).getSkills());
    }

}
