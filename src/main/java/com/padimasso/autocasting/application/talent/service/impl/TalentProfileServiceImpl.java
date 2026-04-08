package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.talent.dto.request.SkillsPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.PublicProfileResponse;
import com.padimasso.autocasting.application.talent.dto.response.TalentProfileResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.CreditRepository;
import com.padimasso.autocasting.application.talent.repository.EducationRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.TalentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class TalentProfileServiceImpl implements TalentProfileService {

    private static final String PROFILE_NOT_FOUND = "profile.not_found";
    private final AuthContext authContext;
    private final TalentProfileRepository talentProfileRepository;
    private final UserRepository userRepository;
    private final SiteMetadataResolver siteMetadataResolver;
    private final CreditRepository creditRepository;
    private final TalentProfileMapper talentProfileMapper;
    private final EducationRepository educationRepository;

    @Override
    public TalentProfileResponse getMyProfile() {
        UserEntity user = authContext.getCurrentUserOrThrow();

        var foundProfile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));

        return talentProfileMapper.toProfileResponse(foundProfile, user);
    }

    @Override
    public PublicProfileResponse getProfileBySlug(String slug) {

        TalentProfileEntity foundProfile = talentProfileRepository
            .findByDefaultSlugOrPremiumSlug(slug, slug)
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));

        return talentProfileMapper.toPublicProfileResponse(foundProfile);
    }

    @Override
    public Set<SiteMetadataObject> patchMySkills(SkillsPatchRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        var foundProfile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));

        if (request.skillIds() != null) {
            Set<UUID> ids = request.skillIds();
            if (ids.isEmpty()) {
                foundProfile.getSkills().clear();
            } else {
                foundProfile.setSkills(siteMetadataResolver.resolveSkillsOrThrow(ids));
            }
        }

        return TalentProfileMapper.mapToSiteMetadataObjectList(talentProfileRepository.save(foundProfile).getSkills());
    }

}
