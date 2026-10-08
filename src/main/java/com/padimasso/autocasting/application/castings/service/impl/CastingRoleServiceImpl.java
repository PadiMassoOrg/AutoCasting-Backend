package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.auth.context.EmployerContext;
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
import com.padimasso.autocasting.application.castings.service.internal.CastingRoleDuplicator;
import com.padimasso.autocasting.application.common.dto.LastModifiedResponse;
import com.padimasso.autocasting.application.shared.util.PageHydration;
import com.padimasso.autocasting.application.talent.service.MediaStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private final CastingRoleDuplicator castingRoleDuplicator;
    private final EmployerContext employerContext;

    @Override
    @Transactional
    public CastingRoleResponse createCastingRole(CastingRoleRequest request) {
        CastingEntity casting = findOwnedCastingOrThrow(request.castingId());
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
        findOwnedCastingOrThrow(filter.castingId());

        var pageable = PageRequest.of(
            page,
            Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
            Sort.by(Sort.Direction.DESC, "createdAt", "id")
        );

        return PageHydration.hydrate(
                castingRoleRepository.findAll(CastingRoleSpecs.fromEmployerFilter(filter), pageable),
                CastingRoleEntity::getId,
                castingRoleRepository::findAllWithDetailsByIdIn
            )
            .getContent()
            .stream()
            .map(castingMapper::toEmployerRoleCardResponse)
            .toList();
    }

    @Override
    @Transactional
    public CastingRoleResponse updateCastingRole(UUID roleId, CastingRoleRequest request) {
        CastingRoleEntity role = findOwnedRoleOrThrow(roleId);

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
        CastingRoleEntity role = findOwnedRoleOrThrow(roleId);
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
        return castingMapper.toRoleResponse(findOwnedRoleOrThrow(roleId));
    }

    @Override
    @Transactional
    public CastingRoleResponse duplicateCastingRole(UUID roleId, String roleName) {
        CastingRoleEntity sourceRole = findOwnedRoleOrThrow(roleId);
        assertDraftEditable(sourceRole.getCasting());

        CastingRoleEntity duplicatedRole = castingRoleDuplicator.duplicate(sourceRole, roleName);
        return castingMapper.toRoleResponse(castingRoleRepository.save(duplicatedRole));
    }

    private UUID currentEmployerProfileId() {
        return employerContext.getCurrentEmployerOrThrow().employerProfile().getId();
    }

    private CastingEntity findOwnedCastingOrThrow(UUID castingId) {
        return castingRepository.findByIdAndEmployerProfile_IdAndDeletedFalse(castingId, currentEmployerProfileId())
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));
    }

    // Roles of another employer's casting are reported as not found, never as forbidden.
    private CastingRoleEntity findOwnedRoleOrThrow(UUID roleId) {
        UUID employerProfileId = currentEmployerProfileId();
        return castingRoleRepository.findByIdAndDeletedFalse(roleId)
            .filter(role -> role.getCasting() != null
                && role.getCasting().getEmployerProfile() != null
                && employerProfileId.equals(role.getCasting().getEmployerProfile().getId()))
            .orElseThrow(() -> new IllegalArgumentException(CASTING_ROLE_NOT_FOUND));
    }

    private void assertDraftEditable(CastingEntity casting) {
        String statusCode = casting != null && casting.getStatus() != null ? casting.getStatus().getStringCode() : null;
        if (!CASTING_STATUS_DRAFT.equals(statusCode)) {
            throw new IllegalStateException(CASTINGS_ONLY_DRAFT_EDITABLE);
        }
    }
}
