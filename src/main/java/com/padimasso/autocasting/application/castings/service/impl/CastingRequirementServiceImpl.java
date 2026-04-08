package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingRequirementsFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRequirementBulkRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRequirementResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRequirementCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.section.CastingRequirementsSectionResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingRequirementEntity;
import com.padimasso.autocasting.application.castings.model.CastingRequirementsSectionEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRequirementRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRequirementsSectionRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.repository.specification.CastingRequirementSpecs;
import com.padimasso.autocasting.application.castings.service.CastingRequirementService;
import com.padimasso.autocasting.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.*;

@Service
@RequiredArgsConstructor
public class CastingRequirementServiceImpl implements CastingRequirementService {

    private final CastingRequirementsSectionRepository requirementsSectionRepository;
    private final CastingRequirementRepository requirementRepository;
    private final CastingRoleRepository castingRoleRepository;
    private final CastingMapper castingMapper;

    @Override
    public CastingRequirementsSectionResponse getBySectionId(UUID sectionId) {
        CastingRequirementsSectionEntity foundSection = requirementsSectionRepository.findById(sectionId)
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_SECTION_NOT_FOUND));
        return castingMapper.toRequirementsSectionResponsePublic(foundSection);
    }

    @Override
    @Transactional
    public List<CastingRequirementCardResponse> getRequirementsBySectionId(EmployerCastingRequirementsFilter incomingFilter, int page, int size) {
        var spec = CastingRequirementSpecs.fromEmployerFilter(incomingFilter);

        var pageable = PageRequest.of(
            page,
            Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
            Sort.by(Sort.Direction.DESC, "createdAt", "id")
        );

        var result = requirementRepository.findAll(spec, pageable);

        return result.getContent()
            .stream()
            .map(castingMapper::toRequirementCardResponse)
            .toList();
    }

    @Override
    @Transactional
    public List<CastingRequirementCardResponse> createRequirementsBulk(CastingRequirementBulkRequest request) {

        if (!Boolean.TRUE.equals(request.requiresAudio()) && !Boolean.TRUE.equals(request.requiresVideo())) {
            throw new IllegalArgumentException(GENERAL_REQUIRED_ONE);
        }

        List<UUID> roleIds = request.roleIds().stream()
            .filter(Objects::nonNull)
            .distinct()
            .toList();

        if (roleIds.isEmpty()) {
            throw new IllegalArgumentException(CASTING_ROLE_REQUIRED);
        }

        var section = requirementsSectionRepository.findById(request.requirementsSectionId())
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_SECTION_NOT_FOUND));

        if (section.getCasting() == null || section.getCasting().getId() == null) {
            throw new IllegalStateException(CASTINGS_NOT_FOUND);
        }

        UUID castingId = section.getCasting().getId();

        var roles = castingRoleRepository.findAllByRolesSection_Casting_IdAndIdInAndDeletedFalse(castingId, roleIds);
        Set<UUID> foundRoleIds = roles.stream().map(CastingRoleEntity::getId).collect(Collectors.toSet());
        List<UUID> missingRoleIds = roleIds.stream().filter(id -> !foundRoleIds.contains(id)).toList();

        if (!missingRoleIds.isEmpty()) {
            throw ApiException.badRequest(CASTINGS_ROLE_MISMATCH, missingRoleIds);
        }

        Map<UUID, String> roleNameById = roles.stream()
            .collect(Collectors.toMap(CastingRoleEntity::getId, CastingRoleEntity::getRoleName));


        var existing = requirementRepository
            .findAllByCastingRequirementsSection_IdAndCastingRole_IdInAndDeletedFalse(section.getId(), roleIds);

        if (!existing.isEmpty()) {
            UUID conflictRoleId = existing.getFirst().getCastingRole().getId();
            String roleName = roleNameById.getOrDefault(conflictRoleId, conflictRoleId.toString());
            throw ApiException.conflict(CASTING_ROLE_REQUIREMENT_ALREADY_EXISTS, roleName);
        }

        String description = readNullableTrimmed(request.description());

        List<CastingRequirementEntity> toSave = roles.stream()
            .map(role -> CastingRequirementEntity.builder()
                .castingRequirementsSection(section)
                .castingRole(role)
                .requiresAudio(Boolean.TRUE.equals(request.requiresAudio()))
                .requiresVideo(Boolean.TRUE.equals(request.requiresVideo()))
                .description(description)
                .build()
            )
            .toList();

        try {
            var saved = requirementRepository.saveAll(toSave);
            return saved.stream().map(castingMapper::toRequirementCardResponse).toList();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException(CASTING_ROLE_REQUIREMENT_ALREADY_EXISTS);
        }
    }

    @Override
    @Transactional
    public CastingRequirementResponse updateCastingRequirement(UUID requirementId, CastingRequirementBulkRequest request) {
        if (!Boolean.TRUE.equals(request.requiresAudio()) && !Boolean.TRUE.equals(request.requiresVideo())) {
            throw new IllegalArgumentException(GENERAL_REQUIRED_ONE);
        }
        CastingRequirementEntity requirement = requirementRepository.findById(requirementId)
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_SECTION_NOT_FOUND));
        if (requirement.getCastingRequirementsSection() == null || requirement.getCastingRequirementsSection().getId() == null) {
            throw new IllegalStateException(CASTINGS_SECTION_NOT_FOUND);
        }
        if (!requirement.getCastingRequirementsSection().getId().equals(request.requirementsSectionId())) {
            throw new IllegalArgumentException(CASTINGS_SECTION_MISMATCH);
        }
        if (request.roleIds() == null || request.roleIds().isEmpty()) {
            throw new IllegalArgumentException(CASTING_ROLE_REQUIRED);
        }
        UUID currentRoleId = requirement.getCastingRole() != null ? requirement.getCastingRole().getId() : null;
        if (currentRoleId == null) {
            throw new IllegalStateException(CASTING_ROLE_NOT_FOUND);
        }
        if (!request.roleIds().contains(currentRoleId)) {
            throw new IllegalArgumentException(CASTINGS_ROLE_MISMATCH);
        }

        requirement.setRequiresAudio(Boolean.TRUE.equals(request.requiresAudio()));
        requirement.setRequiresVideo(Boolean.TRUE.equals(request.requiresVideo()));
        requirement.setDescription(readNullableTrimmed(request.description()));

        return castingMapper.toRequirementResponse(requirementRepository.save(requirement));
    }

    @Override
    @Transactional
    public void deleteCastingRequirement(UUID requirementId) {
        if (!requirementRepository.existsById(requirementId)) {
            throw new IllegalArgumentException(CASTINGS_SECTION_NOT_FOUND);
        }
        requirementRepository.deleteById(requirementId);
    }

    private String readNullableTrimmed(JsonNullable<String> v) {
        if (v == null || !v.isPresent()) return null;
        String s = v.get();
        if (s == null) return null;
        s = s.trim();
        return s.isBlank() ? null : s;
    }
}
