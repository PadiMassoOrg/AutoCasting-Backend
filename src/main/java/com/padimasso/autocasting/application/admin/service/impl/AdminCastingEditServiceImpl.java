package com.padimasso.autocasting.application.admin.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleCreateRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleDeleteRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleDuplicateRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingRoleUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingStatusRequest;
import com.padimasso.autocasting.application.admin.dto.request.AdminCastingUpdateRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingDetailsResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingRowResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminCastingMapper;
import com.padimasso.autocasting.application.admin.service.AdminCastingEditService;
import com.padimasso.autocasting.application.admin.util.AdminCastingEditability;
import com.padimasso.autocasting.application.admin.util.ProfileChangeDiff;
import com.padimasso.autocasting.application.applications.repository.CastingApplicationRepository;
import com.padimasso.autocasting.application.applications.repository.projection.ApplicationStatusCountProjection;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.service.CastingMediaCleanupService;
import com.padimasso.autocasting.application.castings.service.internal.CastingDataApplier;
import com.padimasso.autocasting.application.castings.service.internal.CastingPublishability;
import com.padimasso.autocasting.application.castings.service.internal.CastingRoleDuplicator;
import com.padimasso.autocasting.application.castings.service.internal.CastingStatusTransitionPolicy;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.dto.HistoryChangeEntry;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.talent.service.MediaStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.CASTING_APPLICATION_STATUS_SELECTED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_CLOSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static com.padimasso.autocasting.config.AppConstants.PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTING_ROLE_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_INVALID_STATUS_TRANSITION;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_LAST_ROLE_REQUIRED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_NOT_FOUND;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_ROLE_HAS_SELECTED_APPLICATIONS;
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
    private final CastingApplicationRepository castingApplicationRepository;
    private final CastingDataApplier castingDataApplier;
    private final CastingRoleDuplicator castingRoleDuplicator;
    private final MediaStorageService mediaStorageService;
    private final CastingStatusTransitionPolicy castingStatusTransitionPolicy;
    private final SiteMetadataResolver siteMetadataResolver;
    private final CastingMediaCleanupService castingMediaCleanupService;
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
        CastingRoleEntity role = findEditableRoleOrThrow(roleId);

        if (!role.getCasting().getId().equals(request.role().castingId())) {
            throw new IllegalArgumentException(CASTINGS_ROLE_MISMATCH);
        }

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

    @Override
    @Transactional
    public CastingRoleResponse createCastingRole(AdminCastingRoleCreateRequest request) {
        var casting = castingRepository.findByIdAndDeletedFalse(request.role().castingId())
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));
        AdminCastingEditability.assertEditable(casting);

        CastingRoleEntity role = CastingRoleEntity.builder().casting(casting).build();
        castingDataApplier.applyRoleData(role, request.role());
        // The admin form does not manage photos: a client-sent URL is never stored.
        role.setReferencePhotoUrl(null);
        var saved = castingRoleRepository.saveAndFlush(role);

        var after = adminCastingMapper.toRoleResponse(saved);
        var changes = ProfileChangeDiff.diff(objectMapper, "role", null, roleSnapshot(after));
        recordHistory(EntityType.CASTING_ROLE, saved.getId(), request.reason(), changes);

        return after;
    }

    @Override
    @Transactional
    public CastingRoleResponse duplicateCastingRole(UUID roleId, AdminCastingRoleDuplicateRequest request) {
        CastingRoleEntity source = findEditableRoleOrThrow(roleId);

        var saved = castingRoleRepository.saveAndFlush(castingRoleDuplicator.duplicate(source, request.roleName()));

        var after = adminCastingMapper.toRoleResponse(saved);
        var changes = new ArrayList<>(ProfileChangeDiff.diff(objectMapper, "role", null, roleSnapshot(after)));
        changes.add(new HistoryChangeEntry("role.duplicatedFrom", null, roleId.toString()));
        changes.add(new HistoryChangeEntry("role.duplicatedFromName", null, source.getRoleName()));
        recordHistory(EntityType.CASTING_ROLE, saved.getId(), request.reason(), changes);

        return after;
    }

    @Override
    @Transactional
    public void deleteCastingRole(UUID roleId, AdminCastingRoleDeleteRequest request) {
        CastingRoleEntity role = findEditableRoleOrThrow(roleId);
        var casting = role.getCasting();

        Map<String, Long> applications = applicationsByStatus(roleId);
        if (applications.getOrDefault(CASTING_APPLICATION_STATUS_SELECTED, 0L) > 0) {
            throw new IllegalStateException(CASTINGS_ROLE_HAS_SELECTED_APPLICATIONS);
        }

        boolean isDraft = CASTING_STATUS_DRAFT.equals(casting.getStatus().getStringCode());
        if (!isDraft && castingRoleRepository.countByCasting_IdAndDeletedFalse(casting.getId()) <= 1) {
            throw new IllegalStateException(CASTINGS_LAST_ROLE_REQUIRED);
        }

        JsonNode before = roleSnapshot(adminCastingMapper.toRoleResponse(role));

        castingRoleRepository.softDelete(role);
        mediaStorageService.deleteByPublicUrl(role.getReferencePhotoUrl());

        var changes = new ArrayList<>(ProfileChangeDiff.diff(objectMapper, "role", before, null));
        changes.add(new HistoryChangeEntry("role.deleted", false, true));
        if (!applications.isEmpty()) {
            // The applications stay stored: the entry tells which ones the talents will keep seeing.
            changes.add(new HistoryChangeEntry("role.applicationsKept", null, applications));
        }
        recordHistory(EntityType.CASTING_ROLE, roleId, request.reason(), changes);
    }

    @Override
    @Transactional
    public AdminCastingRowResponse changeCastingStatus(UUID castingId, AdminCastingStatusRequest request) {
        var casting = castingRepository.findByIdAndDeletedFalse(castingId)
            // Proposal castings belong to the proposals module and never go through this flow.
            .filter(found -> found.getEmployerProfile() == null
                || !PROPOSALS_SYSTEM_EMPLOYER_PROFILE_ID.equals(found.getEmployerProfile().getId()))
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));

        String currentStatusCode = casting.getStatus() != null ? casting.getStatus().getStringCode() : null;
        String targetStatusCode = request.status().trim();
        boolean publishable = CastingPublishability.isPublishable(casting);

        var allowed = castingStatusTransitionPolicy.allowedNextStatuses(
            currentStatusCode, casting.getApplicationDeadline(), publishable
        );
        if (!allowed.contains(targetStatusCode)) {
            // Tells an incomplete casting or a passed deadline apart from a plain forbidden transition.
            if (CASTING_STATUS_PUBLISHED.equals(targetStatusCode)) {
                castingStatusTransitionPolicy.assertCanPublish(currentStatusCode, casting.getApplicationDeadline(), publishable);
            }
            throw new IllegalStateException(CASTINGS_INVALID_STATUS_TRANSITION);
        }

        casting.setStatus(siteMetadataResolver.resolveCastingStatusByCodeOrThrow(targetStatusCode));
        var saved = castingRepository.saveAndFlush(casting);

        if (CASTING_STATUS_CLOSED.equals(targetStatusCode) && saved.getEmployerProfile() != null) {
            castingMediaCleanupService.deleteCastingFolder(saved.getEmployerProfile().getId(), saved.getId());
        }

        recordHistory(
            EntityType.CASTING,
            castingId,
            request.reason(),
            List.of(new HistoryChangeEntry(
                "casting.status",
                Map.of("stringCode", currentStatusCode),
                Map.of("stringCode", targetStatusCode)
            ))
        );

        return adminCastingMapper.toRowResponse(saved);
    }

    private CastingRoleEntity findEditableRoleOrThrow(UUID roleId) {
        CastingRoleEntity role = castingRoleRepository.findByIdAndDeletedFalse(roleId)
            .filter(found -> found.getCasting() != null && !found.getCasting().isDeleted())
            .orElseThrow(() -> new IllegalArgumentException(CASTING_ROLE_NOT_FOUND));
        AdminCastingEditability.assertEditable(role.getCasting());
        return role;
    }

    private Map<String, Long> applicationsByStatus(UUID roleId) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (ApplicationStatusCountProjection row : castingApplicationRepository.countByStatusForRole(roleId)) {
            result.put(row.getStatusCode(), row.getTotal());
        }
        return result;
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
