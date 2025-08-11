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

        if (request.heightCm() != null) {
            characteristics.setHeightCm(request.heightCm());
        }
        if (request.weightKg() != null) {
            characteristics.setWeightKg(request.weightKg());
        }
        if (request.hairColorId() != null) {
            ColorOptionEntity color = colorOptionRepository.findById(request.hairColorId())
                .orElseThrow(() -> new IllegalArgumentException("site_metadata.color.not_found"));
            characteristics.setHairColor(color);
        }
        if (request.eyeColorId() != null) {
            ColorOptionEntity color = colorOptionRepository.findById(request.eyeColorId())
                .orElseThrow(() -> new IllegalArgumentException("site_metadata.color.not_found"));
            characteristics.setEyeColor(color);
        }
        if (request.chestCm() != null) {
            characteristics.setChestCm(request.chestCm());
        }
        if (request.waistCm() != null) {
            characteristics.setWaistCm(request.waistCm());
        }
        if (request.hipCm() != null) {
            characteristics.setHipCm(request.hipCm());
        }
        if (request.shirtSize() != null) {
            characteristics.setShirtSize(request.shirtSize());
        }
        if (request.pantSize() != null) {
            characteristics.setPantSize(request.pantSize());
        }
        if (request.dressSize() != null) {
            characteristics.setDressSize(request.dressSize());
        }
        if (request.shoeSize() != null) {
            characteristics.setShoeSize(request.shoeSize());
        }
        characteristics.setTattoo(request.tattoo());
        characteristics.setPassport(request.passport());
        characteristics.setDrivingLicense(request.drivingLicense());
        if (request.dietOptionId() != null) {
            DietOptionEntity diet = dietOptionRepository.findById(request.dietOptionId())
                .orElseThrow(() -> new IllegalArgumentException("site_metadata.diet.not_found"));
            characteristics.setDietOption(diet);
        }

        return profileMapper.toCharacteristicsResponse(characteristicsRepository.save(characteristics));
    }
}
