package com.padimasso.autocasting.application.sitemetadata.service;

import com.padimasso.autocasting.application.sitemetadata.model.*;

import java.util.Set;
import java.util.UUID;

public interface SiteMetadataResolver {
    CastingStatusOptionEntity resolveCastingStatusByCodeOrThrow(String stringCode);

    CastingApplicationStatusOptionEntity resolveCastingApplicationStatusByCodeOrThrow(String stringCode);

    GenderOptionEntity resolveGenderOrThrow(UUID id);

    Set<ProfessionEntity> resolveProfessionsOrThrow(Set<UUID> ids);

    Set<SkillEntity> resolveSkillsOrThrow(Set<UUID> ids);

    RoleTypeOptionEntity resolveRoleTypeOrThrow(UUID id);

    EthnicityOptionEntity resolveEthnicityOrThrow(UUID id);

    ColorOptionEntity resolveColorOrThrow(UUID id);

    DietOptionEntity resolveDietOrThrow(UUID id);

    ProductionTypeEntity resolveProductionTypeOrThrow(UUID id);

    CompanyTypeOptionEntity resolveCompanyTypeOrThrow(UUID id);

    SocialMediaOptionEntity resolveSocialMediaOptionOrThrow(UUID id);

    ProjectTypeOptionEntity resolveProjectTypeOrThrow(UUID id);

    CastingModalityOptionEntity resolveCastingModalityOrThrow(UUID id);

    CastingCompensationTypeOptionEntity resolveCastingCompensationTypeOrThrow(UUID id);

    CastingCompensationTypeOptionEntity resolveCastingCompensationTypeByCodeOrThrow(String stringCode);

    GenderOptionEntity resolveGenderByCodeOrThrow(String stringCode);

    CastingSectionStatusOptionEntity resolveCastingSectionStatusByCodeOrThrow(String stringCode);

    PayRateTypeOptionEntity resolvePayRateTypeOrThrow(UUID id);

    PayRateTypeOptionEntity resolvePayRateTypeByCodeOrThrow(String stringCode);

    CurrencyOptionEntity resolveCurrencyOrThrow(UUID id);

    CurrencyOptionEntity resolveCurrencyByCodeOrThrow(String stringCode);
}
