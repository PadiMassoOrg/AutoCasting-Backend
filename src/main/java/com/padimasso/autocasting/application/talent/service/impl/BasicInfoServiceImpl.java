package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import com.padimasso.autocasting.application.talent.dto.request.BasicInfoPatchRequest;
import com.padimasso.autocasting.application.talent.dto.response.BasicInfoResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.model.BasicInfoEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.BasicInfoRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.BasicInfoService;
import com.padimasso.autocasting.application.talent.TalentProfessionConstraints;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BasicInfoServiceImpl implements BasicInfoService {

    private final AuthContext authContext;
    private final TalentProfileRepository talentProfileRepository;
    private final BasicInfoRepository basicInfoRepository;
    private final SiteMetadataResolver siteMetadataResolver;
    private final TalentProfileMapper talentProfileMapper;

    @Transactional
    @Override
    public BasicInfoResponse patchMyBasicInfo(BasicInfoPatchRequest req) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        TalentProfileEntity profile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));
        BasicInfoEntity basicInfo = basicInfoRepository.findByTalentProfileId(profile.getId())
            .orElseGet(() -> basicInfoRepository.save(BasicInfoEntity.builder().talentProfile(profile).build()));

        if (req.stageName() != null) {
            basicInfo.setStageName(TextNormalizer.normalizeNullable(req.stageName()));
        }
        if (req.genderId() != null) {
            basicInfo.setGender(siteMetadataResolver.resolveGenderOrThrow(req.genderId()));
        }
        if (req.birthDate() != null) {
            basicInfo.setBirthDate(req.birthDate());
        }
        if (req.professionIds() != null) {
            Set<UUID> ids = req.professionIds();
            if (ids.size() > TalentProfessionConstraints.MAX_PROFESSIONS) {
                throw new IllegalArgumentException("talent.professions_max");
            }
            if (ids.isEmpty()) {
                basicInfo.getProfessions().clear();
            } else {
                basicInfo.setProfessions(siteMetadataResolver.resolveProfessionsOrThrow(ids));
            }
        }

        return talentProfileMapper.toBasicInfoResponse(basicInfoRepository.save(basicInfo));
    }
}
