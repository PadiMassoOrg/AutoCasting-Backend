package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.sitemetadata.model.ColorOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.DietOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.EthnicityOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.ColorOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.DietOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.EthnicityOptionRepository;
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

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CharacteristicsServiceImpl implements CharacteristicsService {

    private final AuthContext authContext;
    private final TalentProfileRepository talentProfileRepository;
    private final CharacteristicsRepository characteristicsRepository;
    private final ColorOptionRepository colorOptionRepository;
    private final EthnicityOptionRepository ethnicityOptionRepository;
    private final DietOptionRepository dietOptionRepository;
    private final TalentProfileMapper talentProfileMapper;

    @Override
    public CharacteristicsResponse patchMyCharacteristics(CharacteristicsPatchRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        TalentProfileEntity profile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
        CharacteristicsEntity characteristics = characteristicsRepository.findByTalentProfileId(profile.getId())
            .orElseGet(() -> characteristicsRepository.save(CharacteristicsEntity.builder().talentProfile(profile).build()));

        if (request.heightCm().isPresent()) characteristics.setHeightCm(request.heightCm().orElse(null));
        if (request.ethnicityId() != null) {
            EthnicityOptionEntity ethnicityOption = ethnicityOptionRepository.findById(request.ethnicityId())
                .orElseThrow(() -> new IllegalArgumentException("sitemetadata.ethnicity.not_found"));
            characteristics.setEthnicity(ethnicityOption);
        }
        if (request.weightKg().isPresent()) characteristics.setWeightKg(request.weightKg().orElse(null));
        if (request.hairColorId() != null) {
            ColorOptionEntity color = colorOptionRepository.findById(request.hairColorId())
                .orElseThrow(() -> new IllegalArgumentException("sitemetadata.color.not_found"));
            characteristics.setHairColor(color);
        }
        if (request.eyeColorId() != null) {
            ColorOptionEntity color = colorOptionRepository.findById(request.eyeColorId())
                .orElseThrow(() -> new IllegalArgumentException("sitemetadata.color.not_found"));
            characteristics.setEyeColor(color);
        }
        if (request.chestCm().isPresent()) characteristics.setChestCm(request.chestCm().orElse(null));
        if (request.waistCm().isPresent()) characteristics.setWaistCm(request.waistCm().orElse(null));
        if (request.hipCm().isPresent()) characteristics.setHipCm(request.hipCm().orElse(null));
        if (request.shirtSize().isPresent()) characteristics.setShirtSize(request.shirtSize().orElse(null));
        if (request.pantSize().isPresent()) characteristics.setPantSize(request.pantSize().orElse(null));
        if (request.dressSize().isPresent()) characteristics.setDressSize(request.dressSize().orElse(null));
        if (request.shoeSize().isPresent()) characteristics.setShoeSize(request.shoeSize().orElse(null));
        if (request.tattoo() != null) characteristics.setTattoo(request.tattoo());
        if (request.passport() != null) characteristics.setPassport(request.passport());
        if (request.drivingLicense() != null) characteristics.setDrivingLicense(request.drivingLicense());
        if (request.dietOptionId() != null) {
            DietOptionEntity diet = dietOptionRepository.findById(request.dietOptionId())
                .orElseThrow(() -> new IllegalArgumentException("sitemetadata.diet.not_found"));
            characteristics.setDietOption(diet);
        }

        return talentProfileMapper.toCharacteristicsResponse(characteristicsRepository.save(characteristics));
    }
}
