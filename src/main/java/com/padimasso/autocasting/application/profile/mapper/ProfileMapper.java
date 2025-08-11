package com.padimasso.autocasting.application.profile.mapper;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.profile.dto.response.*;
import com.padimasso.autocasting.application.profile.model.*;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.model.ColorOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.SiteMetadataBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

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
            toCharacteristicsResponse(profile.getCharacteristics()),
            mapToSiteMetadataObjectList(profile.getSkills()),
            mapToSiteMetadataObjectList(profile.getProfessions()),
            profile.getCredits().stream().map(this::toCreditResponse).toList()
        );
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
            toCharacteristicsResponse(profile.getCharacteristics()),
            mapToSiteMetadataObjectList(profile.getSkills()),
            mapToSiteMetadataObjectList(profile.getProfessions()),
            profile.getCredits().stream().map(this::toCreditResponse).toList()
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

    private <T extends SiteMetadataBase> SiteMetadataObject mapToSiteMetadataObject(T entity) {
        if (entity == null) {
            return null;
        }

        String category = null;
        if (entity instanceof ColorOptionEntity colorEntity) {
            category = colorEntity.getCategory();
        }
        return new SiteMetadataObject(entity.getId(), entity.getStringCode(), category);
    }

    private <T extends SiteMetadataBase> List<SiteMetadataObject> mapToSiteMetadataObjectList(Set<T> entities) {
        return entities.stream()
            .map(entity -> {
                String category = null;
                if (entity instanceof ColorOptionEntity colorEntity) {
                    category = colorEntity.getCategory();
                }
                return new SiteMetadataObject(entity.getId(), entity.getStringCode(), category);
            })
            .toList();
    }
}
