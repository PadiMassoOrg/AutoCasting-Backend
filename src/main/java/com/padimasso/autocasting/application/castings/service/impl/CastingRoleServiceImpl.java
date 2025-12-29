package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.model.CastingRolesSectionEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRolesSectionRepository;
import com.padimasso.autocasting.application.castings.service.CastingRoleService;
import com.padimasso.autocasting.application.sitemetadata.repository.GenderOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.ProfessionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.RoleTypeOptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingRoleServiceImpl implements CastingRoleService {

    private final CastingRolesSectionRepository castingRolesSectionRepository;
    private final CastingRoleRepository castingRoleRepository;
    private final ProfessionRepository professionRepository;
    private final RoleTypeOptionRepository roleTypeOptionRepository;
    private final GenderOptionRepository genderOptionRepository;
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

        //TODO: Remuneration as UNPAID default, probablemente Requirements a ver.
        // TODO: Characteristics, Skills, etc.

        return castingMapper.toRoleResponse(castingRoleRepository.save(newRole));
    }
}
