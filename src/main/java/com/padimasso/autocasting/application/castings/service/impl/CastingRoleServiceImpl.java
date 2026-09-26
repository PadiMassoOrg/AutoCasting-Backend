package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.dto.EmployerCastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.request.CastingRoleRequest;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRoleEmployerCardResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.repository.specification.CastingRoleSpecs;
import com.padimasso.autocasting.application.castings.service.CastingRoleService;
import com.padimasso.autocasting.application.castings.service.internal.CastingDataApplier;
import com.padimasso.autocasting.application.common.dto.LastModifiedResponse;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import com.padimasso.autocasting.application.talent.service.MediaStorageService;
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
    private final CastingRoleRepository castingRoleRepository;
    private final CastingRepository castingRepository;
    private final CastingMapper castingMapper;
    private final MediaStorageService mediaStorageService;
    private final CastingDataApplier castingDataApplier;

    @Override
    @Transactional
    public CastingRoleResponse createCastingRole(CastingRoleRequest request) {
        CastingEntity casting = castingRepository.findByIdAndDeletedFalse(request.castingId())
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));
        assertDraftEditable(casting);

        CastingRoleEntity role = CastingRoleEntity.builder()
            .casting(casting)
            .build();

        castingDataApplier.applyRoleData(role, request);
        return castingMapper.toRoleResponse(castingRoleRepository.save(role));
    }

    @Override
    @Transactional
    public List<CastingRoleEmployerCardResponse> getCastingRolesByCastingId(EmployerCastingRoleFilter filter, int page, int size) {
        var pageable = PageRequest.of(
            page,
            Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
            Sort.by(Sort.Direction.DESC, "createdAt", "id")
        );

        return castingRoleRepository.findAll(CastingRoleSpecs.fromEmployerFilter(filter), pageable)
            .getContent()
            .stream()
            .map(castingMapper::toEmployerRoleCardResponse)
            .toList();
    }

    @Override
    @Transactional
    public CastingRoleResponse updateCastingRole(UUID roleId, CastingRoleRequest request) {
        CastingRoleEntity role = castingRoleRepository.findByIdAndDeletedFalse(roleId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_ROLE_NOT_FOUND));

        if (role.getCasting() == null || !role.getCasting().getId().equals(request.castingId())) {
            throw new IllegalArgumentException(CASTINGS_ROLE_MISMATCH);
        }
        assertDraftEditable(role.getCasting());

        castingDataApplier.applyRoleData(role, request);
        return castingMapper.toRoleResponse(castingRoleRepository.save(role));
    }

    @Override
    @Transactional
    public LastModifiedResponse deleteCastingRole(UUID roleId) {
        CastingRoleEntity role = castingRoleRepository.findByIdAndDeletedFalse(roleId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_ROLE_NOT_FOUND));
        assertDraftEditable(role.getCasting());

        UUID castingId = role.getCasting() != null ? role.getCasting().getId() : null;
        castingRoleRepository.softDelete(role);
        mediaStorageService.deleteByPublicUrl(role.getReferencePhotoUrl());

        return new LastModifiedResponse(
            castingId == null
                ? null
                : castingRepository.findByIdAndDeletedFalse(castingId).map(CastingEntity::getModifiedAt).orElse(null)
        );
    }

    @Override
    public CastingRoleResponse getById(UUID roleId) {
        return castingRoleRepository.findByIdAndDeletedFalse(roleId)
            .map(castingMapper::toRoleResponse)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_ROLE_NOT_FOUND));
    }

    @Override
    @Transactional
    public CastingRoleResponse duplicateCastingRole(UUID roleId, String roleName) {
        CastingRoleEntity sourceRole = castingRoleRepository.findByIdAndDeletedFalse(roleId)
            .orElseThrow(() -> new IllegalArgumentException(CASTING_ROLE_NOT_FOUND));
        assertDraftEditable(sourceRole.getCasting());
        String duplicatedRoleName = TextNormalizer.normalizeNullable(roleName);

        CastingRoleEntity duplicatedRole = CastingRoleEntity.builder()
            .casting(sourceRole.getCasting())
            .roleName(duplicatedRoleName != null ? duplicatedRoleName : sourceRole.getRoleName())
            .roleType(sourceRole.getRoleType())
            .gender(sourceRole.getGender())
            .ageMin(sourceRole.getAgeMin())
            .ageMax(sourceRole.getAgeMax())
            .description(sourceRole.getDescription())
            .payRateType(sourceRole.getPayRateType())
            .currency(sourceRole.getCurrency())
            .amount(sourceRole.getAmount())
            .remunerationNotes(sourceRole.getRemunerationNotes())
            .requiresAudio(sourceRole.isRequiresAudio())
            .requiresVideo(sourceRole.isRequiresVideo())
            .requirementDescription(sourceRole.getRequirementDescription())
            .ethnicity(sourceRole.getEthnicity())
            .tattoo(sourceRole.getTattoo())
            .passport(sourceRole.getPassport())
            .drivingLicense(sourceRole.getDrivingLicense())
            // Deliberately NOT copied: two roles must never share the same Supabase object.
            // referencePhotoUrl points at a single file, and deleting/replacing it from either
            // role would delete it out from under the other. The duplicated role starts with
            // no photo — the employer re-uploads (even the same image) to get its own URL.
            .referencePhotoUrl(null)
            .professions(new HashSet<>(sourceRole.getProfessions() == null ? Set.of() : sourceRole.getProfessions()))
            .skills(new HashSet<>(sourceRole.getSkills() == null ? Set.of() : sourceRole.getSkills()))
            .build();

        castingDataApplier.validateRole(duplicatedRole);
        return castingMapper.toRoleResponse(castingRoleRepository.save(duplicatedRole));
    }

    private void assertDraftEditable(CastingEntity casting) {
        String statusCode = casting != null && casting.getStatus() != null ? casting.getStatus().getStringCode() : null;
        if (!CASTING_STATUS_DRAFT.equals(statusCode)) {
            throw new IllegalStateException(CASTINGS_ONLY_DRAFT_EDITABLE);
        }
    }
}
