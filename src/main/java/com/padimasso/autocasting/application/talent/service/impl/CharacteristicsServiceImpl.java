package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import com.padimasso.autocasting.application.talent.dto.request.CharacteristicsPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.CharacteristicsResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.model.CharacteristicsEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.CharacteristicsRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.CharacteristicsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CharacteristicsServiceImpl implements CharacteristicsService {

    private final AuthContext authContext;
    private final TalentProfileRepository talentProfileRepository;
    private final CharacteristicsRepository characteristicsRepository;
    private final SiteMetadataResolver siteMetadataResolver;
    private final TalentProfileMapper talentProfileMapper;

    @Override
    public CharacteristicsResponse patchMyCharacteristics(CharacteristicsPatchRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        TalentProfileEntity profile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));
        CharacteristicsEntity characteristics = characteristicsRepository.findByTalentProfileId(profile.getId())
            .orElseGet(() -> characteristicsRepository.save(CharacteristicsEntity.builder().talentProfile(profile).build()));

        if (request.heightCm().isPresent()) {
            characteristics.setHeightCm(request.heightCm().orElse(null));
        }
        if (request.ethnicityId() != null) {
            characteristics.setEthnicity(siteMetadataResolver.resolveEthnicityOrThrow(request.ethnicityId()));
        }
        if (request.weightKg().isPresent()) {
            characteristics.setWeightKg(request.weightKg().orElse(null));
        }
        if (request.hairColorId() != null) {
            characteristics.setHairColor(siteMetadataResolver.resolveColorOrThrow(request.hairColorId()));
        }
        if (request.eyeColorId() != null) {
            characteristics.setEyeColor(siteMetadataResolver.resolveColorOrThrow(request.eyeColorId()));
        }
        if (request.chestCm().isPresent()) {
            characteristics.setChestCm(TextNormalizer.normalizeNullable(request.chestCm().orElse(null)));
        }
        if (request.waistCm().isPresent()) {
            characteristics.setWaistCm(TextNormalizer.normalizeNullable(request.waistCm().orElse(null)));
        }
        if (request.hipCm().isPresent()) {
            characteristics.setHipCm(TextNormalizer.normalizeNullable(request.hipCm().orElse(null)));
        }
        if (request.shirtSize().isPresent()) {
            characteristics.setShirtSize(TextNormalizer.normalizeNullable(request.shirtSize().orElse(null)));
        }
        if (request.pantSize().isPresent()) {
            characteristics.setPantSize(TextNormalizer.normalizeNullable(request.pantSize().orElse(null)));
        }
        if (request.dressSize().isPresent()) {
            characteristics.setDressSize(TextNormalizer.normalizeNullable(request.dressSize().orElse(null)));
        }
        if (request.shoeSize().isPresent()) {
            characteristics.setShoeSize(TextNormalizer.normalizeNullable(request.shoeSize().orElse(null)));
        }
        if (request.tattoo() != null) characteristics.setTattoo(request.tattoo());
        if (request.passport() != null) characteristics.setPassport(request.passport());
        if (request.drivingLicense() != null) characteristics.setDrivingLicense(request.drivingLicense());
        if (request.dietOptionId() != null) {
            characteristics.setDietOption(siteMetadataResolver.resolveDietOrThrow(request.dietOptionId()));
        }

        return talentProfileMapper.toCharacteristicsResponse(characteristicsRepository.save(characteristics));
    }
}
