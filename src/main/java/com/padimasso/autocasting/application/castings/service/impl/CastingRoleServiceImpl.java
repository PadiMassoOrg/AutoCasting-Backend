package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRoleEmployerCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRolesSectionResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingRoleCharacteristicsEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleRemunerationEntity;
import com.padimasso.autocasting.application.castings.model.CastingRolesSectionEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRequirementRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRemunerationRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRolesSectionRepository;
import com.padimasso.autocasting.application.castings.repository.specification.CastingRoleSpecs;
import com.padimasso.autocasting.application.castings.service.CastingRoleService;
import com.padimasso.autocasting.application.castings.service.internal.CastingRemunerationSectionStatusService;
import com.padimasso.autocasting.application.castings.service.internal.CastingStatusService;
import com.padimasso.autocasting.application.common.dto.LastModifiedResponse;
import com.padimasso.autocasting.application.sitemetadata.model.*;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.*;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.*;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingRoleServiceImpl implements CastingRoleService {

    private final CastingRolesSectionRepository castingRolesSectionRepository;
    private final CastingRoleRepository castingRoleRepository;
    private final CastingRequirementRepository castingRequirementRepository;
    private final CastingRoleRemunerationRepository castingRoleRemunerationRepository;
    private final CastingRepository castingRepository;
    private final SiteMetadataResolver siteMetadataResolver;
    private final CastingRemunerationSectionStatusService remunerationSectionStatusService;
    private final CastingStatusService castingStatusService;
    private final CastingMapper castingMapper;

    @Override
    @Transactional
    public CastingRoleResponse createCastingRole(CastingRoleRequest request) {

        CastingRolesSectionEntity foundSection = castingRolesSectionRepository.findById(request.rolesSectionId())
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_SECTION_NOT_FOUND));

        Set<UUID> professionIds = request.professionIds() == null ? Set.of() : request.professionIds();
        var foundProfessions = new HashSet<>(siteMetadataResolver.resolveProfessionsOrThrow(professionIds));

        RoleTypeOptionEntity foundRoleType = siteMetadataResolver.resolveRoleTypeOrThrow(request.roleTypeId());

        GenderOptionEntity foundGender = siteMetadataResolver.resolveGenderOrThrow(request.genderId());

        PayRateTypeOptionEntity payRateTypeUnpaid = siteMetadataResolver.resolvePayRateTypeByCodeOrThrow(PAY_RATE_TYPE_UNPAID);

        CurrencyOptionEntity currencyARS = siteMetadataResolver.resolveCurrencyByCodeOrThrow(CURRENCY_ARS);

        var newRole = CastingRoleEntity.builder()
            .rolesSection(foundSection)
            .roleName(request.roleName())
            .professions(foundProfessions)
            .roleType(foundRoleType)
            .gender(foundGender)
            .ageMin(request.ageMin())
            .ageMax(request.ageMax())
            .build();

        if (request.description().isPresent()) newRole.setDescription(TextNormalizer.normalizeNullable(request.description().orElse(null)));
        if (request.skillIds().isPresent()) {
            Set<UUID> skillIds = request.skillIds().get();
            var foundSkills = new HashSet<>(siteMetadataResolver.resolveSkillsOrThrow(skillIds));
            newRole.setSkills(foundSkills);
        }

        CastingRoleCharacteristicsEntity newCharacteristics = CastingRoleCharacteristicsEntity.builder()
            .castingRole(newRole)
            .build();

        if (request.characteristics().isPresent()) {
            var ch = request.characteristics().get();
            if (ch.heightCm().isPresent()) newCharacteristics.setHeightCm(ch.heightCm().orElse(null));
            if (ch.ethnicityId() != null) {
                EthnicityOptionEntity ethnicityOption = siteMetadataResolver.resolveEthnicityOrThrow(ch.ethnicityId());
                newCharacteristics.setEthnicity(ethnicityOption);
            }
            if (ch.weightKg().isPresent()) newCharacteristics.setWeightKg(ch.weightKg().orElse(null));
            if (ch.hairColorId() != null) {
                ColorOptionEntity color = siteMetadataResolver.resolveColorOrThrow(ch.hairColorId());
                newCharacteristics.setHairColor(color);
            }
            if (ch.eyeColorId() != null) {
                ColorOptionEntity color = siteMetadataResolver.resolveColorOrThrow(ch.eyeColorId());
                newCharacteristics.setEyeColor(color);
            }
            if (ch.chestCm().isPresent()) newCharacteristics.setChestCm(TextNormalizer.normalizeNullable(ch.chestCm().orElse(null)));
            if (ch.waistCm().isPresent()) newCharacteristics.setWaistCm(TextNormalizer.normalizeNullable(ch.waistCm().orElse(null)));
            if (ch.hipCm().isPresent()) newCharacteristics.setHipCm(TextNormalizer.normalizeNullable(ch.hipCm().orElse(null)));
            if (ch.shirtSize().isPresent()) newCharacteristics.setShirtSize(TextNormalizer.normalizeNullable(ch.shirtSize().orElse(null)));
            if (ch.pantSize().isPresent()) newCharacteristics.setPantSize(TextNormalizer.normalizeNullable(ch.pantSize().orElse(null)));
            if (ch.dressSize().isPresent()) newCharacteristics.setDressSize(TextNormalizer.normalizeNullable(ch.dressSize().orElse(null)));
            if (ch.shoeSize().isPresent()) newCharacteristics.setShoeSize(TextNormalizer.normalizeNullable(ch.shoeSize().orElse(null)));
            if (ch.tattoo().isPresent()) newCharacteristics.setTattoo(ch.tattoo().orElse(null));
            if (ch.passport().isPresent()) newCharacteristics.setPassport(ch.passport().orElse(null));
            if (ch.drivingLicense().isPresent()) newCharacteristics.setDrivingLicense(ch.drivingLicense().orElse(null));
            if (ch.dietOptionId() != null) {
                DietOptionEntity diet = siteMetadataResolver.resolveDietOrThrow(ch.dietOptionId());
                newCharacteristics.setDietOption(diet);
            }
        }

        CastingRoleRemunerationEntity newRemuneration = CastingRoleRemunerationEntity.builder()
            .castingRole(newRole)
            .payRateType(payRateTypeUnpaid)
            .currency(currencyARS)
            .build();

        newRole.setCharacteristics(newCharacteristics);
        newRole.setRemuneration(newRemuneration);

        CastingRoleEntity saved = castingRoleRepository.save(newRole);

        if (foundSection.getId() != null) {
            updateSectionStatus(foundSection.getId());
        }

        UUID castingId = foundSection.getCasting() != null ? foundSection.getCasting().getId() : null;
        if (castingId != null) {
            remunerationSectionStatusService.recomputeForCasting(castingId);
            castingStatusService.recomputeAfterSectionChange(castingId);
        }

        return castingMapper.toRoleResponse(saved);
    }

    @Override
    public CastingRolesSectionResponse getBySectionId(UUID sectionId) {
        CastingRolesSectionEntity foundSection = castingRolesSectionRepository.findById(sectionId)
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_SECTION_NOT_FOUND));
        return castingMapper.toRolesSectionResponsePublic(foundSection);
    }

    @Override
    @Transactional
    public List<CastingRoleEmployerCardResponse> getCastingRolesBySectionId(EmployerCastingRoleFilter incomingFilter, int page, int size) {
        var spec = CastingRoleSpecs.fromEmployerFilter(incomingFilter);

        var pageable = PageRequest.of(
            page,
            Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
            Sort.by(Sort.Direction.DESC, "createdAt", "id")
        );

        var result = castingRoleRepository.findAll(spec, pageable);

        return result.getContent()
            .stream()
            .map(castingMapper::toEmployerRoleCardResponse)
            .toList();
    }

    @Override
    @Transactional
    public CastingRoleResponse updateCastingRole(UUID roleId, CastingRoleRequest request) {
        CastingRoleEntity role = castingRoleRepository.findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_ROLE_NOT_FOUND));
        if (role.getRolesSection() == null || role.getRolesSection().getId() == null) {
            throw new IllegalStateException(CASTINGS_SECTION_NOT_FOUND);
        }
        if (!role.getRolesSection().getId().equals(request.rolesSectionId())) {
            throw new IllegalArgumentException(CASTINGS_SECTION_MISMATCH);
        }

        var foundRoleType = siteMetadataResolver.resolveRoleTypeOrThrow(request.roleTypeId());
        var foundGender = siteMetadataResolver.resolveGenderOrThrow(request.genderId());
        Set<UUID> professionIds = request.professionIds() == null ? Set.of() : request.professionIds();
        var foundProfessions = new HashSet<>(siteMetadataResolver.resolveProfessionsOrThrow(professionIds));

        role.setRoleName(request.roleName());
        role.setRoleType(foundRoleType);
        role.setGender(foundGender);
        role.setAgeMin(request.ageMin());
        role.setAgeMax(request.ageMax());
        role.setProfessions(foundProfessions);

        role.setDescription(TextNormalizer.normalizeNullable(request.description().orElse(null)));
        if (request.skillIds().isPresent()) {
            Set<UUID> skillIds = request.skillIds().get();
            var foundSkills = new HashSet<>(siteMetadataResolver.resolveSkillsOrThrow(skillIds));
            role.setSkills(foundSkills);
        }

        CastingRoleCharacteristicsEntity characteristics = role.getCharacteristics();
        if (characteristics == null) {
            characteristics = CastingRoleCharacteristicsEntity.builder()
                .castingRole(role)
                .build();
            role.setCharacteristics(characteristics);
        }

        CastingRoleRemunerationEntity remuneration = role.getRemuneration();
        if (remuneration == null) {
            remuneration = CastingRoleRemunerationEntity.builder()
                .castingRole(role)
                .build();
            role.setRemuneration(remuneration);
        }

        if (request.characteristics().isPresent()) {
            var ch = request.characteristics().get();

            if (ch.heightCm().isPresent()) characteristics.setHeightCm(ch.heightCm().orElse(null));
            if (ch.weightKg().isPresent()) characteristics.setWeightKg(ch.weightKg().orElse(null));
            if (ch.chestCm().isPresent()) characteristics.setChestCm(TextNormalizer.normalizeNullable(ch.chestCm().orElse(null)));
            if (ch.waistCm().isPresent()) characteristics.setWaistCm(TextNormalizer.normalizeNullable(ch.waistCm().orElse(null)));
            if (ch.hipCm().isPresent()) characteristics.setHipCm(TextNormalizer.normalizeNullable(ch.hipCm().orElse(null)));
            if (ch.shirtSize().isPresent()) characteristics.setShirtSize(TextNormalizer.normalizeNullable(ch.shirtSize().orElse(null)));
            if (ch.pantSize().isPresent()) characteristics.setPantSize(TextNormalizer.normalizeNullable(ch.pantSize().orElse(null)));
            if (ch.dressSize().isPresent()) characteristics.setDressSize(TextNormalizer.normalizeNullable(ch.dressSize().orElse(null)));
            if (ch.shoeSize().isPresent()) characteristics.setShoeSize(TextNormalizer.normalizeNullable(ch.shoeSize().orElse(null)));

            if (ch.tattoo().isPresent()) characteristics.setTattoo(ch.tattoo().orElse(null));
            if (ch.passport().isPresent()) characteristics.setPassport(ch.passport().orElse(null));
            if (ch.drivingLicense().isPresent()) characteristics.setDrivingLicense(ch.drivingLicense().orElse(null));

            if (ch.ethnicityId() != null) {
                EthnicityOptionEntity ethnicityOption = siteMetadataResolver.resolveEthnicityOrThrow(ch.ethnicityId());
                characteristics.setEthnicity(ethnicityOption);
            } else {
                characteristics.setEthnicity(null);
            }

            if (ch.hairColorId() != null) {
                ColorOptionEntity color = siteMetadataResolver.resolveColorOrThrow(ch.hairColorId());
                characteristics.setHairColor(color);
            } else {
                characteristics.setHairColor(null);
            }

            if (ch.eyeColorId() != null) {
                ColorOptionEntity color = siteMetadataResolver.resolveColorOrThrow(ch.eyeColorId());
                characteristics.setEyeColor(color);
            } else {
                characteristics.setEyeColor(null);
            }

            if (ch.dietOptionId() != null) {
                DietOptionEntity diet = siteMetadataResolver.resolveDietOrThrow(ch.dietOptionId());
                characteristics.setDietOption(diet);
            } else {
                characteristics.setDietOption(null);
            }
        }

        CastingRoleEntity saved = castingRoleRepository.save(role);

        UUID castingId = role.getRolesSection() != null && role.getRolesSection().getCasting() != null
            ? role.getRolesSection().getCasting().getId()
            : null;

        if (castingId != null) {
            castingStatusService.recomputeAfterSectionChange(castingId);
        }

        return castingMapper.toRoleResponse(saved);
    }

    @Override
    @Transactional
    public LastModifiedResponse deleteCastingRole(UUID roleId) {
        CastingRoleEntity role = castingRoleRepository.findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_ROLE_NOT_FOUND));

        UUID sectionId = role.getRolesSection() != null ? role.getRolesSection().getId() : null;

        UUID castingId = null;
        if (role.getRolesSection() != null && role.getRolesSection().getCasting() != null) {
            castingId = role.getRolesSection().getCasting().getId();
        }

        CastingRoleRemunerationEntity remuneration = role.getRemuneration();
        if (remuneration != null && !remuneration.isDeleted()) {
            castingRoleRemunerationRepository.softDelete(remuneration);
        }

        var requirements = castingRequirementRepository.findAllByCastingRole_IdAndDeletedFalse(roleId);
        for (var requirement : requirements) {
            castingRequirementRepository.softDelete(requirement);
        }

        castingRoleRepository.softDelete(role);

        if (sectionId != null) {
            updateSectionStatus(sectionId);
        }

        if (castingId != null) {
            remunerationSectionStatusService.recomputeForCasting(castingId);
            castingStatusService.recomputeAfterSectionChange(castingId);
            castingRepository.touchModifiedAt(castingId);
            return new LastModifiedResponse(castingRepository.findModifiedAtById(castingId));
        }

        return new LastModifiedResponse(null);
    }

    private void updateSectionStatus(UUID sectionId) {
        CastingRolesSectionEntity section = castingRolesSectionRepository.findById(sectionId)
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_SECTION_NOT_FOUND));

        long activeCount = castingRoleRepository.countByRolesSectionIdAndDeletedFalse(sectionId);

        String nextStatusCode = activeCount >= 1
            ? CASTING_SECTION_STATUS_COMPLETED
            : CASTING_SECTION_STATUS_IN_PROGRESS;

        CastingSectionStatusOptionEntity status = siteMetadataResolver.resolveCastingSectionStatusByCodeOrThrow(nextStatusCode);
        section.setSectionStatus(status);
        castingRolesSectionRepository.save(section);
    }
}
