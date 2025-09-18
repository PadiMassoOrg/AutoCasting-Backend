package com.padimasso.autocasting.application.profile.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.profile.dto.request.CharacteristicsPatchRequest;
import com.padimasso.autocasting.application.profile.dto.response.CharacteristicsResponse;
import com.padimasso.autocasting.application.profile.mapper.ProfileMapper;
import com.padimasso.autocasting.application.profile.model.CharacteristicsEntity;
import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import com.padimasso.autocasting.application.profile.repository.CharacteristicsRepository;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
import com.padimasso.autocasting.application.profile.service.CharacteristicsService;
import com.padimasso.autocasting.application.sitemetadata.model.ColorOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.DietOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.ColorOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.DietOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CharacteristicsServiceImpl implements CharacteristicsService {

    private final AuthContext authContext;
    private final ProfileRepository profileRepository;
    private final CharacteristicsRepository characteristicsRepository;
    private final ColorOptionRepository colorOptionRepository;
    private final DietOptionRepository dietOptionRepository;
    private final ProfileMapper profileMapper;

    @Override
    public CharacteristicsResponse patchMyCharacteristics(CharacteristicsPatchRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        ProfileEntity profile = profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
        CharacteristicsEntity characteristics = characteristicsRepository.findByProfileId(profile.getId())
            .orElseGet(() -> characteristicsRepository.save(CharacteristicsEntity.builder().profile(profile).build()));

        if (request.heightCm().isPresent()) characteristics.setHeightCm(request.heightCm().orElse(null));
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

        return profileMapper.toCharacteristicsResponse(characteristicsRepository.save(characteristics));
    }
}
