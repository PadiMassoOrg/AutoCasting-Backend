package com.padimasso.autocasting.application.sitemetadata.service.impl;

import com.padimasso.autocasting.application.sitemetadata.model.*;
import com.padimasso.autocasting.application.sitemetadata.repository.*;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SiteMetadataResolverImpl implements SiteMetadataResolver {

    private final CastingStatusOptionRepository castingStatusOptionRepository;
    private final CastingApplicationStatusOptionRepository castingApplicationStatusOptionRepository;
    private final GenderOptionRepository genderOptionRepository;
    private final ProfessionRepository professionRepository;
    private final SkillRepository skillRepository;
    private final RoleTypeOptionRepository roleTypeOptionRepository;
    private final EthnicityOptionRepository ethnicityOptionRepository;
    private final ColorOptionRepository colorOptionRepository;
    private final DietOptionRepository dietOptionRepository;
    private final ProductionTypeRepository productionTypeRepository;
    private final CompanyTypeOptionRepository companyTypeOptionRepository;
    private final SocialMediaOptionRepository socialMediaOptionRepository;
    private final ProjectTypeOptionRepository projectTypeOptionRepository;
    private final CastingModalityOptionRepository castingModalityOptionRepository;
    private final CastingCompensationTypeOptionRepository castingCompensationTypeOptionRepository;
    private final CastingSectionStatusOptionRepository castingSectionStatusOptionRepository;
    private final PayRateTypeOptionRepository payRateTypeOptionRepository;
    private final CurrencyOptionRepository currencyOptionRepository;

    @Override
    public CastingStatusOptionEntity resolveCastingStatusByCodeOrThrow(String stringCode) {
        return castingStatusOptionRepository.findByStringCode(stringCode)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.casting_status.not_found"));
    }

    @Override
    public CastingApplicationStatusOptionEntity resolveCastingApplicationStatusByCodeOrThrow(String stringCode) {
        return castingApplicationStatusOptionRepository.findByStringCode(stringCode)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.application_status.not_found"));
    }

    @Override
    public GenderOptionEntity resolveGenderOrThrow(UUID id) {
        return genderOptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.gender.not_found"));
    }

    @Override
    public Set<ProfessionEntity> resolveProfessionsOrThrow(Set<UUID> ids) {
        var found = new HashSet<>(professionRepository.findAllByIdIn(ids));
        if (found.size() != ids.size()) {
            throw new IllegalArgumentException("sitemetadata.profession.invalid_ids");
        }
        return found;
    }

    @Override
    public Set<SkillEntity> resolveSkillsOrThrow(Set<UUID> ids) {
        var found = new HashSet<>(skillRepository.findAllByIdIn(ids));
        if (found.size() != ids.size()) {
            throw new IllegalArgumentException("sitemetadata.skill.invalid_ids");
        }
        return found;
    }

    @Override
    public RoleTypeOptionEntity resolveRoleTypeOrThrow(UUID id) {
        return roleTypeOptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.casting_role.not_found"));
    }

    @Override
    public EthnicityOptionEntity resolveEthnicityOrThrow(UUID id) {
        return ethnicityOptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.ethnicity.not_found"));
    }

    @Override
    public ColorOptionEntity resolveColorOrThrow(UUID id) {
        return colorOptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.color.not_found"));
    }

    @Override
    public DietOptionEntity resolveDietOrThrow(UUID id) {
        return dietOptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.diet.not_found"));
    }

    @Override
    public ProductionTypeEntity resolveProductionTypeOrThrow(UUID id) {
        return productionTypeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.production_type.not_found"));
    }

    @Override
    public CompanyTypeOptionEntity resolveCompanyTypeOrThrow(UUID id) {
        return companyTypeOptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.company_type.not_found"));
    }

    @Override
    public SocialMediaOptionEntity resolveSocialMediaOptionOrThrow(UUID id) {
        return socialMediaOptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("social_media_option.not_found"));
    }

    @Override
    public ProjectTypeOptionEntity resolveProjectTypeOrThrow(UUID id) {
        return projectTypeOptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.project_type.not_found"));
    }

    @Override
    public CastingModalityOptionEntity resolveCastingModalityOrThrow(UUID id) {
        return castingModalityOptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.casting_modality.not_found"));
    }

    @Override
    public CastingCompensationTypeOptionEntity resolveCastingCompensationTypeOrThrow(UUID id) {
        return castingCompensationTypeOptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.casting_compensation_type.not_found"));
    }

    @Override
    public CastingCompensationTypeOptionEntity resolveCastingCompensationTypeByCodeOrThrow(String stringCode) {
        return castingCompensationTypeOptionRepository.findByStringCode(stringCode)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.casting_compensation_type.not_found"));
    }

    @Override
    public GenderOptionEntity resolveGenderByCodeOrThrow(String stringCode) {
        return genderOptionRepository.findByStringCode(stringCode)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.gender.not_found"));
    }

    @Override
    public CastingSectionStatusOptionEntity resolveCastingSectionStatusByCodeOrThrow(String stringCode) {
        return castingSectionStatusOptionRepository.findByStringCode(stringCode)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.casting_section_status.not_found"));
    }

    @Override
    public PayRateTypeOptionEntity resolvePayRateTypeOrThrow(UUID id) {
        return payRateTypeOptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.pay_rate_type.not_found"));
    }

    @Override
    public PayRateTypeOptionEntity resolvePayRateTypeByCodeOrThrow(String stringCode) {
        return payRateTypeOptionRepository.findByStringCode(stringCode)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.pay_rate_type.not_found"));
    }

    @Override
    public CurrencyOptionEntity resolveCurrencyOrThrow(UUID id) {
        return currencyOptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.currency.not_found"));
    }

    @Override
    public CurrencyOptionEntity resolveCurrencyByCodeOrThrow(String stringCode) {
        return currencyOptionRepository.findByStringCode(stringCode)
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.currency.not_found"));
    }
}
