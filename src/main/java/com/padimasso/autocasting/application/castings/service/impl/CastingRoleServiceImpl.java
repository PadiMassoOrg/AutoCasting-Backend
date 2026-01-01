package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleEmployerCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingRoleCharacteristicsEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleRemunerationEntity;
import com.padimasso.autocasting.application.castings.model.CastingRolesSectionEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRolesSectionRepository;
import com.padimasso.autocasting.application.castings.repository.specification.CastingRoleSpecs;
import com.padimasso.autocasting.application.castings.service.CastingRoleService;
import com.padimasso.autocasting.application.sitemetadata.model.ColorOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.DietOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.EthnicityOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingRoleServiceImpl implements CastingRoleService {

    private final CastingRolesSectionRepository castingRolesSectionRepository;
    private final CastingRoleRepository castingRoleRepository;
    private final ProfessionRepository professionRepository;
    private final RoleTypeOptionRepository roleTypeOptionRepository;
    private final GenderOptionRepository genderOptionRepository;
    private final SkillRepository skillRepository;
    private final EthnicityOptionRepository ethnicityOptionRepository;
    private final ColorOptionRepository colorOptionRepository;
    private final DietOptionRepository dietOptionRepository;
    private final CastingMapper castingMapper;

    @Transactional
    @Override
    public CastingRoleResponse createCastingRole(CastingRoleRequest request) {
        CastingRolesSectionEntity foundSection = castingRolesSectionRepository.findById(request.rolesSectionId())
            .orElseThrow(() -> new IllegalArgumentException("castings.not_found"));
        Set<UUID> professionIds = request.professionIds();
        var foundProfessions = new HashSet<>(professionRepository.findAllByIdIn(professionIds));
        var foundRoleType = roleTypeOptionRepository.findById(request.roleTypeId())
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.casting_role.not_found"));
        var foundGender = genderOptionRepository.findById(request.genderId())
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.gender.not_found"));

        // Required
        var newRole = CastingRoleEntity.builder()
            .rolesSection(foundSection)
            .roleName(request.roleName())
            .professions(foundProfessions)
            .roleType(foundRoleType)
            .gender(foundGender)
            .ageMin(request.ageMin())
            .ageMax(request.ageMax())
            .build();

        // Optional
        if (request.description().isPresent()) newRole.setDescription(request.description().orElse(null));
        if (request.skillIds().isPresent()) {
            Set<UUID> skillIds = request.skillIds().get();
            var foundSkills = new HashSet<>(skillRepository.findAllByIdIn(skillIds));
            newRole.setSkills(foundSkills);
        }

        // Related Entities
        CastingRoleCharacteristicsEntity newCharacteristics = CastingRoleCharacteristicsEntity.builder()
            .castingRole(newRole)
            .build();

        if (request.characteristics().isPresent()) {
            var ch = request.characteristics().get();
            if (ch.heightCm().isPresent()) newCharacteristics.setHeightCm(ch.heightCm().orElse(null));
            if (ch.ethnicityId() != null) {
                EthnicityOptionEntity ethnicityOption = ethnicityOptionRepository.findById(ch.ethnicityId())
                    .orElseThrow(() -> new IllegalArgumentException("sitemetadata.ethnicity.not_found"));
                newCharacteristics.setEthnicity(ethnicityOption);
            }
            if (ch.weightKg().isPresent()) newCharacteristics.setWeightKg(ch.weightKg().orElse(null));
            if (ch.hairColorId() != null) {
                ColorOptionEntity color = colorOptionRepository.findById(ch.hairColorId())
                    .orElseThrow(() -> new IllegalArgumentException("sitemetadata.color.not_found"));
                newCharacteristics.setHairColor(color);
            }
            if (ch.eyeColorId() != null) {
                ColorOptionEntity color = colorOptionRepository.findById(ch.eyeColorId())
                    .orElseThrow(() -> new IllegalArgumentException("sitemetadata.color.not_found"));
                newCharacteristics.setEyeColor(color);
            }
            if (ch.chestCm().isPresent()) newCharacteristics.setChestCm(ch.chestCm().orElse(null));
            if (ch.waistCm().isPresent()) newCharacteristics.setWaistCm(ch.waistCm().orElse(null));
            if (ch.hipCm().isPresent()) newCharacteristics.setHipCm(ch.hipCm().orElse(null));
            if (ch.shirtSize().isPresent()) newCharacteristics.setShirtSize(ch.shirtSize().orElse(null));
            if (ch.pantSize().isPresent()) newCharacteristics.setPantSize(ch.pantSize().orElse(null));
            if (ch.dressSize().isPresent()) newCharacteristics.setDressSize(ch.dressSize().orElse(null));
            if (ch.shoeSize().isPresent()) newCharacteristics.setShoeSize(ch.shoeSize().orElse(null));
            if (ch.tattoo().isPresent()) newCharacteristics.setTattoo(ch.tattoo().orElse(null));
            if (ch.passport().isPresent()) newCharacteristics.setPassport(ch.passport().orElse(null));
            if (ch.drivingLicense().isPresent()) newCharacteristics.setDrivingLicense(ch.drivingLicense().orElse(null));
            if (ch.dietOptionId() != null) {
                DietOptionEntity diet = dietOptionRepository.findById(ch.dietOptionId())
                    .orElseThrow(() -> new IllegalArgumentException("sitemetadata.diet.not_found"));
                newCharacteristics.setDietOption(diet);
            }
        }

        CastingRoleRemunerationEntity newRemuneration = CastingRoleRemunerationEntity.builder()
            .castingRole(newRole)
            .build();

        newRole.setCharacteristics(newCharacteristics);
        newRole.setRemuneration(newRemuneration);

        return castingMapper.toRoleResponse(castingRoleRepository.save(newRole));
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

        var result = castingRoleRepository.findAll(spec, pageable); // SoftDelete => deleted=false

        return result.getContent()
            .stream()
            .map(castingMapper::toEmployerRoleCardResponse)
            .toList();
    }

    @Override
    @Transactional
    public CastingRoleResponse updateCastingRole(UUID roleId, CastingRoleRequest request) {
        CastingRoleEntity role = castingRoleRepository.findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("castings.role.not_found"));
        if (role.getRolesSection() == null || role.getRolesSection().getId() == null) {
            throw new IllegalStateException("castings.section.not_found");
        }
        if (!role.getRolesSection().getId().equals(request.rolesSectionId())) {
            throw new IllegalArgumentException("castings.section.mismatch");
        }

        var foundRoleType = roleTypeOptionRepository.findById(request.roleTypeId())
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.casting_role.not_found"));
        var foundGender = genderOptionRepository.findById(request.genderId())
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.gender.not_found"));
        Set<UUID> professionIds = request.professionIds();
        var foundProfessions = new HashSet<>(professionRepository.findAllByIdIn(professionIds));

        // Required
        role.setRoleName(request.roleName());
        role.setRoleType(foundRoleType);
        role.setGender(foundGender);
        role.setAgeMin(request.ageMin());
        role.setAgeMax(request.ageMax());
        role.setProfessions(foundProfessions);

        // Optional
        role.setDescription(request.description().orElse(null));

        // 6) Optional: skills (si no vienen => null / empty según tu criterio)
        // Yo lo dejo: si viene -> set; si no viene -> no tocar (conserva)
        // Si preferís que el "no viene" borre, entonces hacé role.setSkills(Collections.emptySet())
        if (request.skillIds().isPresent()) {
            Set<UUID> skillIds = request.skillIds().get();
            var foundSkills = new HashSet<>(skillRepository.findAllByIdIn(skillIds));
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

        // Characteristics
        if (request.characteristics().isPresent()) {
            var ch = request.characteristics().get();

            if (ch.heightCm().isPresent()) characteristics.setHeightCm(ch.heightCm().orElse(null));
            if (ch.weightKg().isPresent()) characteristics.setWeightKg(ch.weightKg().orElse(null));
            if (ch.chestCm().isPresent()) characteristics.setChestCm(ch.chestCm().orElse(null));
            if (ch.waistCm().isPresent()) characteristics.setWaistCm(ch.waistCm().orElse(null));
            if (ch.hipCm().isPresent()) characteristics.setHipCm(ch.hipCm().orElse(null));
            if (ch.shirtSize().isPresent()) characteristics.setShirtSize(ch.shirtSize().orElse(null));
            if (ch.pantSize().isPresent()) characteristics.setPantSize(ch.pantSize().orElse(null));
            if (ch.dressSize().isPresent()) characteristics.setDressSize(ch.dressSize().orElse(null));
            if (ch.shoeSize().isPresent()) characteristics.setShoeSize(ch.shoeSize().orElse(null));

            if (ch.tattoo().isPresent()) characteristics.setTattoo(ch.tattoo().orElse(null));
            if (ch.passport().isPresent()) characteristics.setPassport(ch.passport().orElse(null));
            if (ch.drivingLicense().isPresent()) characteristics.setDrivingLicense(ch.drivingLicense().orElse(null));

            if (ch.ethnicityId() != null) {
                EthnicityOptionEntity ethnicityOption = ethnicityOptionRepository.findById(ch.ethnicityId())
                    .orElseThrow(() -> new IllegalArgumentException("sitemetadata.ethnicity.not_found"));
                characteristics.setEthnicity(ethnicityOption);
            } else {
                characteristics.setEthnicity(null);
            }

            if (ch.hairColorId() != null) {
                ColorOptionEntity color = colorOptionRepository.findById(ch.hairColorId())
                    .orElseThrow(() -> new IllegalArgumentException("sitemetadata.color.not_found"));
                characteristics.setHairColor(color);
            } else {
                characteristics.setHairColor(null);
            }

            if (ch.eyeColorId() != null) {
                ColorOptionEntity color = colorOptionRepository.findById(ch.eyeColorId())
                    .orElseThrow(() -> new IllegalArgumentException("sitemetadata.color.not_found"));
                characteristics.setEyeColor(color);
            } else {
                characteristics.setEyeColor(null);
            }

            if (ch.dietOptionId() != null) {
                DietOptionEntity diet = dietOptionRepository.findById(ch.dietOptionId())
                    .orElseThrow(() -> new IllegalArgumentException("sitemetadata.diet.not_found"));
                characteristics.setDietOption(diet);
            } else {
                characteristics.setDietOption(null);
            }
        }

        CastingRoleEntity saved = castingRoleRepository.save(role);
        return castingMapper.toRoleResponse(saved);
    }

    @Override
    @Transactional
    public void deleteCastingRole(UUID roleId) {
        castingRoleRepository.deleteById(roleId);
    }

}

