package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.sitemetadata.model.GenderOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.GenderOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.ProfessionRepository;
import com.padimasso.autocasting.application.talent.dto.request.BasicInfoPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.BasicInfoResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.model.BasicInfoEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.BasicInfoRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.BasicInfoService;
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
    private final TalentProfileRepository talentProfileRepository;
    private final BasicInfoRepository basicInfoRepository;
    private final ProfessionRepository professionRepository;
    private final GenderOptionRepository genderOptionRepository;
    private final TalentProfileMapper talentProfileMapper;

    @Transactional
    @Override
    public BasicInfoResponse patchMyBasicInfo(BasicInfoPatchRequest req) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        TalentProfileEntity profile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
        BasicInfoEntity basicInfo = basicInfoRepository.findByTalentProfileId(profile.getId())
            .orElseGet(() -> basicInfoRepository.save(BasicInfoEntity.builder().talentProfile(profile).build()));

        if (req.stageName() != null) {
            basicInfo.setStageName(req.stageName().trim());
        }
        if (req.genderId() != null) {
            GenderOptionEntity gender = genderOptionRepository.findById(req.genderId())
                .orElseThrow(() -> new IllegalArgumentException("sitemetadata.color.not_found"));
            basicInfo.setGender(gender);
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
                    throw new IllegalArgumentException("sitemetadata.profession.invalid_ids");
                }
                basicInfo.setProfessions(found);
            }
        }

        return talentProfileMapper.toBasicInfoResponse(basicInfoRepository.save(basicInfo));
    }
}
