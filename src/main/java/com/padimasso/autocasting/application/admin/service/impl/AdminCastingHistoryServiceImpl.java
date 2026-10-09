package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.response.AdminCastingHistoryRowResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminHistoryMapper;
import com.padimasso.autocasting.application.admin.service.AdminCastingHistoryService;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.repository.projection.CastingRoleKeyProjection;
import com.padimasso.autocasting.application.common.dto.PageResponse;
import com.padimasso.autocasting.application.common.model.EntityType;
import com.padimasso.autocasting.application.history.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AdminCastingHistoryServiceImpl implements AdminCastingHistoryService {

    private final CastingRepository castingRepository;
    private final CastingRoleRepository castingRoleRepository;
    private final HistoryService historyService;
    private final AdminHistoryMapper adminHistoryMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminCastingHistoryRowResponse> listCastingHistory(UUID castingId, int page, int size) {
        castingRepository.findByIdAndDeletedFalse(castingId)
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));

        int normalizedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int normalizedPage = Math.max(page, 0);
        var pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by(Sort.Direction.DESC, "createdAt", "id"));

        Map<UUID, String> roleNames = new HashMap<>();
        for (CastingRoleKeyProjection role : castingRoleRepository.findAllRoleKeysIncludingDeletedByCastingId(castingId)) {
            roleNames.put(role.getRoleId(), role.getRoleName());
        }

        var result = historyService.listHistoryByEntityAndRelated(
            EntityType.CASTING, castingId, EntityType.CASTING_ROLE, roleNames.keySet(), pageable
        );

        var items = result.getContent().stream()
            .map(entry -> adminHistoryMapper.toCastingRowResponse(
                entry,
                entry.getEntityType() == EntityType.CASTING_ROLE ? roleNames.get(entry.getEntityId()) : null
            ))
            .toList();

        return adminHistoryMapper.toPageResponse(items, result);
    }
}
