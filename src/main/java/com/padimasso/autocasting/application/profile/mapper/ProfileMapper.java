package com.padimasso.autocasting.application.profile.mapper;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.profile.dto.response.*;
import com.padimasso.autocasting.application.profile.model.*;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.model.ColorOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.SiteMetadataBase;
import com.padimasso.autocasting.application.sitemetadata.model.SkillEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfileMapper {

    public ProfileResponse toProfileResponse(ProfileEntity profile, UserEntity user) {
        return new ProfileResponse(
            profile.getId(),
            user.getRole().getNameStringCode(),
            profile.getPlan().getNameStringCode(),
            profile.getPublicSlug(),
            toBasicInfoResponse(profile.getBasicInfo()),
            toContactResponse(profile.getContact()),
            toSocialMediaResponse(profile.getSocialMedia()),
            toMediaResponse(profile.getMedia()),
            toCharacteristicsResponse(profile.getCharacteristics()),
            mapToSiteMetadataObjectList(profile.getSkills()),
            profile.getCredits().stream().map(this::toCreditResponse).collect(Collectors.toSet()),
            profile.getEducation().stream().map(this::toEducationResponse).collect(Collectors.toSet()));
    }

    public PublicProfileResponse toPublicProfileResponse(ProfileEntity profile) {
        return new PublicProfileResponse(
            profile.getId(),
            profile.getUser().getRole().getNameStringCode(),
            profile.getPlan().getNameStringCode(),
            profile.getPublicSlug(),
            toBasicInfoResponse(profile.getBasicInfo()),
            toContactResponse(profile.getContact()),
            toSocialMediaResponse(profile.getSocialMedia()),
            toMediaResponse(profile.getMedia()),
            toCharacteristicsResponse(profile.getCharacteristics()),
            mapToSiteMetadataObjectList(profile.getSkills()),
            profile.getCredits().stream().map(this::toCreditResponse).collect(Collectors.toSet()),
            profile.getEducation().stream().map(this::toEducationResponse).collect(Collectors.toSet()));
    }

    public BasicInfoResponse toBasicInfoResponse(BasicInfoEntity entity) {
        if (entity == null) return null;
        return new BasicInfoResponse(
            entity.getId(),
            entity.getStageName(),
            entity.getGender(),
            entity.getBirthDate(),
            entity.getProfessions().stream().map(this::mapToSiteMetadataObject).toList()
        );
    }

    public ContactResponse toContactResponse(ContactEntity entity) {
        if (entity == null) return null;
        return new ContactResponse(
            entity.getId(),
            entity.getEmail(),
            entity.getPhoneNumber()
        );
    }

    public SocialMediaResponse toSocialMediaResponse(SocialMediaEntity entity) {
        if (entity == null) return null;
        return new SocialMediaResponse(
            entity.getId(),
            entity.getInstagramUrl(),
            entity.getTikTokUrl()
        );
    }

    public MediaResponse toMediaResponse(MediaEntity entity) {
        if (entity == null) return null;
        return new MediaResponse(
            entity.getId(),
            entity.getHeadshotImageUrl(),
            entity.getFullBodyImageUrl(),
            entity.getOtherPicturesUrl(),
            entity.getIntroductionVideoUrl(),
            entity.getShowReelVideoUrl()
        );
    }

    public CharacteristicsResponse toCharacteristicsResponse(CharacteristicsEntity entity) {
        if (entity == null) return null;
        return new CharacteristicsResponse(
            entity.getId(),
            entity.getHeightCm(),
            entity.getWeightKg(),
            mapToSiteMetadataObject(entity.getHairColor()),
            mapToSiteMetadataObject(entity.getEyeColor()),
            entity.getChestCm(),
            entity.getWaistCm(),
            entity.getHipCm(),
            entity.getShirtSize(),
            entity.getPantSize(),
            entity.getDressSize(),
            entity.getShoeSize(),
            entity.isTattoo(),
            entity.isPassport(),
            entity.isDrivingLicense(),
            mapToSiteMetadataObject(entity.getDietOption())
        );
    }

    public CreditResponse toCreditResponse(CreditEntity entity) {
        if (entity == null) return null;
        return new CreditResponse(
            entity.getId(),
            mapToSiteMetadataObject(entity.getProductionType()),
            entity.getProjectName(),
            entity.getProducerName(),
            entity.getRole(),
            entity.getYear()
        );
    }

    public EducationResponse toEducationResponse(EducationEntity entity) {
        if (entity == null) return null;
        return new EducationResponse(
            entity.getId(),
            entity.getInstitution(),
            entity.getCourseName(),
            entity.getGraduationYear()
        );
    }

    public <T extends SiteMetadataBase> SiteMetadataObject mapToSiteMetadataObject(T entity) {
        if (entity == null) {
            return null;
        }

        String category = null;
        if (entity instanceof ColorOptionEntity colorEntity) {
            category = colorEntity.getCategory();
        }
        if (entity instanceof SkillEntity skillEntity) {
            category = skillEntity.getCategory();
        }
        return new SiteMetadataObject(entity.getId(), entity.getStringCode(), category);
    }

    public <T extends SiteMetadataBase> Set<SiteMetadataObject> mapToSiteMetadataObjectList(Set<T> entities) {
        return entities.stream()
            .map(entity -> {
                String category = null;
                if (entity instanceof ColorOptionEntity colorEntity) {
                    category = colorEntity.getCategory();
                }
                if (entity instanceof SkillEntity skillEntity) {
                    category = skillEntity.getCategory();
                }
                return new SiteMetadataObject(entity.getId(), entity.getStringCode(), category);
            })
            .collect(Collectors.toSet());
    }
}
