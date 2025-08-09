package com.padimasso.autocasting.application.profile.mapper;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.profile.dto.response.BasicInfoResponse;
import com.padimasso.autocasting.application.profile.dto.response.ProfileResponse;
import com.padimasso.autocasting.application.profile.dto.response.PublicProfileResponse;
import com.padimasso.autocasting.application.profile.model.BasicInfoEntity;
import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileMapper {

    public ProfileResponse toProfileResponse(ProfileEntity profile, UserEntity user) {
        return new ProfileResponse(
            profile.getId(),
            user.getEmail(),
            user.getRole().getNameStringCode(),
            profile.getPlan().getNameStringCode(),
            profile.getPublicSlug(),
            toBasicInfoResponse(profile.getBasicInfo())
        );
    }

    public PublicProfileResponse toPublicProfileResponse(ProfileEntity profile) {
        return new PublicProfileResponse(
            profile.getId(),
            profile.getUser().getEmail(),
            profile.getUser().getRole().getNameStringCode(),
            profile.getPlan().getNameStringCode(),
            profile.getPublicSlug(),
            toBasicInfoResponse(profile.getBasicInfo())
        );
    }

    public BasicInfoResponse toBasicInfoResponse(BasicInfoEntity entity) {
        if (entity == null) return null;
        return new BasicInfoResponse(
            entity.getId(),
            entity.getStageName(),
            entity.getGender(),
            entity.getBirthDate()
        );
    }
}
