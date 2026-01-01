package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingRequirementsFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRequirementBulkRequest;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRequirementCardResponse;
import com.padimasso.autocasting.application.castings.model.CastingRequirementEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRequirementRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRequirementsSectionRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.repository.specification.CastingRequirementSpecs;
import com.padimasso.autocasting.application.castings.service.CastingRequirementService;
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

@Service
@RequiredArgsConstructor
public class CastingRequirementServiceImpl implements CastingRequirementService {

    private final CastingRequirementRepository requirementRepository;
    private final CastingRequirementsSectionRepository requirementsSectionRepository;
    private final CastingRoleRepository castingRoleRepository;

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
            .map(this::toCard)
            .toList();
    }

    @Override
    @Transactional
    public List<CastingRequirementCardResponse> createRequirementsBulk(CastingRequirementBulkRequest request) {

        if (!Boolean.TRUE.equals(request.requiresAudio()) && !Boolean.TRUE.equals(request.requiresVideo())) {
            // TODO: Message
            throw new IllegalArgumentException("Debe seleccionar al menos Audio o Video.");
        }

        List<UUID> roleIds = request.roleIds().stream()
            .filter(Objects::nonNull)
            .distinct()
            .toList();

        if (roleIds.isEmpty()) {
            // TODO: Message
            throw new IllegalArgumentException("Debe seleccionar al menos un rol.");
        }

        // TODO: Message
        var section = requirementsSectionRepository.findById(request.requirementsSectionId())
            .orElseThrow(() -> new IllegalArgumentException("castings.requirements.section.not_found"));

        if (section.getCasting() == null || section.getCasting().getId() == null) {
            // TODO: Message
            throw new IllegalStateException("castings.casting.not_found");
        }

        UUID castingId = section.getCasting().getId();

        var roles = castingRoleRepository.findAllByRolesSection_Casting_IdAndIdInAndDeletedFalse(castingId, roleIds);
        Set<UUID> foundRoleIds = roles.stream().map(CastingRoleEntity::getId).collect(Collectors.toSet());
        List<UUID> missingRoleIds = roleIds.stream().filter(id -> !foundRoleIds.contains(id)).toList();

        if (!missingRoleIds.isEmpty()) {
            // TODO: Message
            throw new IllegalArgumentException("Algunos roles no pertenecen al casting: " + missingRoleIds);
        }

        Map<UUID, String> roleNameById = roles.stream()
            .collect(Collectors.toMap(CastingRoleEntity::getId, CastingRoleEntity::getRoleName));


        var existing = requirementRepository
            .findAllByCastingRequirementsSection_IdAndCastingRole_IdInAndDeletedFalse(section.getId(), roleIds);

        if (!existing.isEmpty()) {
            UUID conflictRoleId = existing.getFirst().getCastingRole().getId();
            String roleName = roleNameById.getOrDefault(conflictRoleId, conflictRoleId.toString());
            // TODO: Message
            throw new IllegalArgumentException("Ya existe una requirement para role: " + roleName);
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
            return saved.stream().map(this::toCard).toList();
        } catch (DataIntegrityViolationException ex) {
            // Defensa final (si ya aplicaste el UNIQUE INDEX parcial)
            // TODO: Message
            throw new IllegalArgumentException("Ya existe una requirement para uno de los roles seleccionados.");
        }
    }

    private CastingRequirementCardResponse toCard(CastingRequirementEntity e) {
        return new CastingRequirementCardResponse(
            e.getId(),
            e.getCastingRole() != null ? e.getCastingRole().getId() : null,
            e.getCastingRole() != null ? e.getCastingRole().getRoleName() : null,
            e.isRequiresAudio(),
            e.isRequiresVideo(),
            e.getDescription()
        );
    }

    private String readNullableTrimmed(JsonNullable<String> v) {
        if (v == null || !v.isPresent()) return null;
        String s = v.get();
        if (s == null) return null;
        s = s.trim();
        return s.isBlank() ? null : s;
    }
}
