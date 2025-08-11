package com.padimasso.autocasting.application.profile.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.profile.dto.request.BasicInfoPatchRequest;
import com.padimasso.autocasting.application.profile.dto.response.BasicInfoResponse;
import com.padimasso.autocasting.application.profile.mapper.ProfileMapper;
import com.padimasso.autocasting.application.profile.model.BasicInfoEntity;
import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import com.padimasso.autocasting.application.profile.repository.BasicInfoRepository;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
import com.padimasso.autocasting.application.profile.service.BasicInfoService;
import com.padimasso.autocasting.application.sitemetadata.repository.ProfessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BasicInfoServiceImpl implements BasicInfoService {

    private final AuthContext authContext;
    private final ProfileRepository profileRepository;
    private final BasicInfoRepository basicInfoRepository;
    private final ProfessionRepository professionRepository;
    private final ProfileMapper profileMapper;

    @Transactional
    @Override
    public BasicInfoResponse patchMyBasicInfo(BasicInfoPatchRequest req) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        ProfileEntity profile = profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
        BasicInfoEntity basicInfo = basicInfoRepository.findByProfileId(profile.getId())
            .orElseGet(() -> basicInfoRepository.save(BasicInfoEntity.builder().profile(profile).build()));

        if (req.stageName() != null) {
            basicInfo.setStageName(req.stageName().trim());
        }
        if (req.gender() != null) {
            basicInfo.setGender(req.gender().trim());
        }
        if (req.birthDate() != null) {
            basicInfo.setBirthDate(req.birthDate());
        }
        if (req.professionIds() != null) {
            Set<UUID> ids = req.professionIds();
            if (ids.isEmpty()) {
                basicInfo.getProfessions().clear();
            } else {
                var found = new HashSet<>(professionRepository.findAllByIdIn(ids));
                if (found.size() != ids.size()) {
                    throw new IllegalArgumentException("profile.profession_invalid_ids");
                }
                basicInfo.setProfessions(found);
            }
        }
        
        return profileMapper.toBasicInfoResponse(basicInfoRepository.save(basicInfo));
    }
}
