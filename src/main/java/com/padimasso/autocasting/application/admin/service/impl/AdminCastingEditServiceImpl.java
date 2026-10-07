package com.padimasso.autocasting.application.admin.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingDetailsResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminCastingMapper;
import com.padimasso.autocasting.application.admin.service.AdminCastingEditService;
import com.padimasso.autocasting.application.admin.util.AdminCastingEditability;
import com.padimasso.autocasting.application.admin.util.ProfileChangeDiff;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.service.internal.CastingDataApplier;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.dto.HistoryChangeEntry;
import com.padimasso.autocasting.application.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTING_ROLE_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_ROLE_MISMATCH;

@Service
@RequiredArgsConstructor
public class AdminCastingEditServiceImpl implements AdminCastingEditService {

    private static final List<String> CASTING_EDITABLE_FIELDS = List.of(
        "title", "projectType", "castingModality", "locationText", "applicationDeadline", "hasWardrobeFitting",
        "wardrobeFittingText", "shootingStartDate", "shootingEndDate", "description"
    );
    private static final Set<String> ROLE_BOOKKEEPING_FIELDS = Set.of("castingId", "referencePhotoUrl");
    private static final Set<String> REMUNERATION_BOOKKEEPING_FIELDS = Set.of("isComplete", "complete");

    private final CastingRepository castingRepository;
    private final CastingRoleRepository castingRoleRepository;
    private final CastingDataApplier castingDataApplier;
    private final AdminCastingMapper adminCastingMapper;
    private final HistoryService historyService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public AdminCastingDetailsResponse updateCasting(UUID castingId, AdminCastingUpdateRequest request) {
        var casting = castingRepository.findByIdAndDeletedFalse(castingId)
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));
        AdminCastingEditability.assertEditable(casting);

        JsonNode before = castingSnapshot(adminCastingMapper.toDetailsResponse(casting));

        castingDataApplier.applyCastingData(casting, request.casting());
        var saved = castingRepository.saveAndFlush(casting);

        var after = adminCastingMapper.toDetailsResponse(saved);
        var changes = ProfileChangeDiff.diff(objectMapper, "casting", before, castingSnapshot(after));
        recordHistory(EntityType.CASTING, castingId, request.reason(), changes);

        return after;
    }

    @Override
    @Transactional
    public CastingRoleResponse updateCastingRole(UUID roleId, AdminCastingRoleUpdateRequest request) {
        CastingRoleEntity role = castingRoleRepository.findByIdAndDeletedFalse(roleId)
            .filter(found -> found.getCasting() != null && !found.getCasting().isDeleted())
            .orElseThrow(() -> new IllegalArgumentException(CASTING_ROLE_NOT_FOUND));

        if (!role.getCasting().getId().equals(request.role().castingId())) {
            throw new IllegalArgumentException(CASTINGS_ROLE_MISMATCH);
        }
        AdminCastingEditability.assertEditable(role.getCasting());

        JsonNode before = roleSnapshot(adminCastingMapper.toRoleResponse(role));

        // The admin form does not manage the reference photo: keep the stored one.
        String referencePhotoUrl = role.getReferencePhotoUrl();
        castingDataApplier.applyRoleData(role, request.role());
        role.setReferencePhotoUrl(referencePhotoUrl);
        var saved = castingRoleRepository.saveAndFlush(role);

        var after = adminCastingMapper.toRoleResponse(saved);
        var changes = ProfileChangeDiff.diff(objectMapper, "role", before, roleSnapshot(after));
        recordHistory(EntityType.CASTING_ROLE, roleId, request.reason(), changes);

        return after;
    }

    private void recordHistory(EntityType entityType, UUID entityId, String reason, List<HistoryChangeEntry> changes) {
        if (!changes.isEmpty()) {
            historyService.createHistoryEntry(entityType, entityId, reason, changes);
        }
    }

    private JsonNode castingSnapshot(AdminCastingDetailsResponse details) {
        JsonNode full = objectMapper.valueToTree(details);
        ObjectNode snapshot = objectMapper.createObjectNode();
        CASTING_EDITABLE_FIELDS.forEach(field -> {
            if (full.has(field)) snapshot.set(field, full.get(field));
        });
        return snapshot;
    }

    private JsonNode roleSnapshot(CastingRoleResponse role) {
        ObjectNode snapshot = objectMapper.valueToTree(role);
        snapshot.remove(ROLE_BOOKKEEPING_FIELDS);
        JsonNode remuneration = snapshot.get("remuneration");
        if (remuneration instanceof ObjectNode remunerationNode) {
            remunerationNode.remove(REMUNERATION_BOOKKEEPING_FIELDS);
        }
        return snapshot;
    }
}
