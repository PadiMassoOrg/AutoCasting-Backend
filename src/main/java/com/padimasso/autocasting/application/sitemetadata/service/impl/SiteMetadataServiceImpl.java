package com.padimasso.autocasting.application.sitemetadata.service.impl;

import com.padimasso.autocasting.application.auth.dto.response.RoleResponse;
import com.padimasso.autocasting.application.auth.model.RoleEntity;
import com.padimasso.autocasting.application.auth.repository.RoleRepository;
import com.padimasso.autocasting.application.plan.dto.PlanResponse;
import com.padimasso.autocasting.application.plan.model.PlanEntity;
import com.padimasso.autocasting.application.plan.repository.PlanRepository;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.VersionResponse;
import com.padimasso.autocasting.application.sitemetadata.model.SiteMetadataBase;
import com.padimasso.autocasting.application.sitemetadata.repository.*;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Formatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteMetadataServiceImpl implements SiteMetadataService {
    private final RoleRepository roleRepository;
    private final PlanRepository planRepository;
    private final SkillRepository skillRepository;
    private final ProfessionRepository professionRepository;
    private final GenderOptionRepository genderOptionRepository;
    private final EthnicityOptionRepository ethnicityOptionRepository;
    private final ColorOptionRepository colorOptionRepository;
    private final DietOptionRepository dietOptionRepository;
    private final ProductionTypeRepository productionTypeRepository;
    private final SocialMediaOptionRepository socialMediaOptionRepository;
    private final CompanyTypeOptionRepository companyTypeOptionRepository;
    private final CastingActingModeOptionRepository castingActingModeOptionRepository;
    private final CastingCompensationTypeOptionRepository castingCompensationTypeOptionRepository;
    private final CastingModalityOptionRepository castingModalityOptionRepository;
    private final CastingSectionStatusOptionRepository castingSectionStatusOptionRepository;
    private final CastingStatusOptionRepository castingStatusOptionRepository;
    private final CurrencyOptionRepository currencyOptionRepository;
    private final PayRateTypeOptionRepository payRateTypeOptionRepository;
    private final ProjectTypeOptionRepository projectTypeOptionRepository;
    private final RoleTypeOptionRepository roleTypeOptionRepository;

    public SiteMetadataResponse getSiteMetadata() {
        var version = computeVersion();
        var foundRoleEntities = roleRepository.findAll();
        var foundPlanEntities = planRepository.findAll();
        var foundSkillEntities = skillRepository.findAll();
        var foundProfessionEntities = professionRepository.findAll();
        var foundGenderOptionEntities = genderOptionRepository.findAll();
        var foundEthnicityOptionsEntities = ethnicityOptionRepository.findAll();
        var foundColorOptionEntities = colorOptionRepository.findAll();
        var foundDietOptionEntities = dietOptionRepository.findAll();
        var foundProductionTypeOptionEntities = productionTypeRepository.findAll();
        var foundSocialMediaOptionEntities = socialMediaOptionRepository.findAll();
        var foundCompanyTypeOptionEntities = companyTypeOptionRepository.findAll();
        var foundCastingActingModeOptionEntities = castingActingModeOptionRepository.findAll();
        var foundCastingCompensationTypeOptionEntities = castingCompensationTypeOptionRepository.findAll();
        var foundCastingModalityOptionEntities = castingModalityOptionRepository.findAll();
        var foundCastingSectionStatusOptionEntities = castingSectionStatusOptionRepository.findAll();
        var foundCastingStatusOptionEntities = castingStatusOptionRepository.findAll();
        var foundCurrencyOptionEntities = currencyOptionRepository.findAll();
        var foundPayRateTypeOptionEntities = payRateTypeOptionRepository.findAll();
        var foundProjectTypeOptionEntities = projectTypeOptionRepository.findAll();
        var foundRoleTypeOptionEntities = roleTypeOptionRepository.findAll();

        return new SiteMetadataResponse(
            version,
            mapRoles(foundRoleEntities),
            mapPlans(foundPlanEntities),
            mapToSiteMetadataObject(foundSkillEntities),
            mapToSiteMetadataObject(foundProfessionEntities),
            mapToSiteMetadataObject(foundGenderOptionEntities),
            mapToSiteMetadataObject(foundEthnicityOptionsEntities),
            mapToSiteMetadataObject(foundColorOptionEntities),
            mapToSiteMetadataObject(foundDietOptionEntities),
            mapToSiteMetadataObject(foundProductionTypeOptionEntities),
            mapToSiteMetadataObject(foundSocialMediaOptionEntities),
            mapToSiteMetadataObject(foundCompanyTypeOptionEntities),
            mapToSiteMetadataObject(foundCastingActingModeOptionEntities),
            mapToSiteMetadataObject(foundCastingCompensationTypeOptionEntities),
            mapToSiteMetadataObject(foundCastingModalityOptionEntities),
            mapToSiteMetadataObject(foundCastingSectionStatusOptionEntities),
            mapToSiteMetadataObject(foundCastingStatusOptionEntities),
            mapToSiteMetadataObject(foundCurrencyOptionEntities),
            mapToSiteMetadataObject(foundPayRateTypeOptionEntities),
            mapToSiteMetadataObject(foundProjectTypeOptionEntities),
            mapToSiteMetadataObject(foundRoleTypeOptionEntities)
        );
    }

    @Override
    public VersionResponse getVersionOnly() {
        return new VersionResponse(computeVersion());
    }

    private <T extends SiteMetadataBase> List<SiteMetadataObject> mapToSiteMetadataObject(List<T> entities) {
        return entities.stream()
            .map(entity -> new SiteMetadataObject(
                entity.getId(),
                entity.getStringCode(),
                entity.getCategoryStringCode()
            ))
            .toList();
    }

    private List<RoleResponse> mapRoles(List<RoleEntity> roles) {
        return roles.stream()
            .map(role -> new RoleResponse(
                role.getId(),
                role.getCode(),
                role.getNameStringCode(),
                role.getDescription()
            ))
            .toList();
    }

    private List<PlanResponse> mapPlans(List<PlanEntity> plans) {
        return plans.stream()
            .map(plan -> new PlanResponse(
                plan.getId(),
                plan.getCode(),
                plan.getNameStringCode(),
                plan.getDescription(),
                plan.isAllowsCustomSlug()
            ))
            .toList();
    }

    private String computeVersion() {
        String parts = String.join("|",
            "skills:" + skillRepository.count() + ":" + ts(skillRepository.findMaxModifiedAt()),
            "professions:" + professionRepository.count() + ":" + ts(professionRepository.findMaxModifiedAt()),
            "gender:" + genderOptionRepository.count() + ":" + ts(genderOptionRepository.findMaxModifiedAt()),
            "ethnicity:" + ethnicityOptionRepository.count() + ":" + ts(ethnicityOptionRepository.findMaxModifiedAt()),
            "colors:" + colorOptionRepository.count() + ":" + ts(colorOptionRepository.findMaxModifiedAt()),
            "diet:" + dietOptionRepository.count() + ":" + ts(dietOptionRepository.findMaxModifiedAt()),
            "productionType:" + productionTypeRepository.count() + ":" + ts(productionTypeRepository.findMaxModifiedAt()),
            "roles:" + roleRepository.count() + ":" + ts(roleRepository.findMaxModifiedAt()),
            "plans:" + planRepository.count() + ":" + ts(planRepository.findMaxModifiedAt()),
            "socialMedia:" + socialMediaOptionRepository.count() + ":" + ts(socialMediaOptionRepository.findMaxModifiedAt()),
            "companyType:" + companyTypeOptionRepository.count() + ":" + ts(companyTypeOptionRepository.findMaxModifiedAt()),
            "castingActingMode:" + castingActingModeOptionRepository.count() + ":" + ts(castingActingModeOptionRepository.findMaxModifiedAt()),
            "castingCompensationType:" + castingCompensationTypeOptionRepository.count() + ":" + ts(castingCompensationTypeOptionRepository.findMaxModifiedAt()),
            "castingModality:" + castingModalityOptionRepository.count() + ":" + ts(castingModalityOptionRepository.findMaxModifiedAt()),
            "castingSectionStatus:" + castingSectionStatusOptionRepository.count() + ":" + ts(castingSectionStatusOptionRepository.findMaxModifiedAt()),
            "castingStatus:" + castingStatusOptionRepository.count() + ":" + ts(castingStatusOptionRepository.findMaxModifiedAt()),
            "currency:" + currencyOptionRepository.count() + ":" + ts(currencyOptionRepository.findMaxModifiedAt()),
            "payRateType:" + payRateTypeOptionRepository.count() + ":" + ts(payRateTypeOptionRepository.findMaxModifiedAt()),
            "projectType:" + projectTypeOptionRepository.count() + ":" + ts(projectTypeOptionRepository.findMaxModifiedAt()),
            "roleType:" + roleTypeOptionRepository.count() + ":" + ts(roleTypeOptionRepository.findMaxModifiedAt())
        );
        return sha256(parts);
    }

    private String ts(Instant i) {
        return i == null ? "0" : String.valueOf(i.toEpochMilli());
    }

    private String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(s.getBytes(StandardCharsets.UTF_8));
            try (Formatter formatter = new Formatter()) {
                for (byte b : hash) formatter.format("%02x", b);
                return formatter.toString();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot compute sitemetadata version", e);
        }
    }

}
