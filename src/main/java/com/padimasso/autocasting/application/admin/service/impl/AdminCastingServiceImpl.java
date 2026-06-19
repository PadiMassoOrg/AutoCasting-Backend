package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.response.AdminCastingDetailsResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingsPageResponse;
import com.padimasso.autocasting.application.admin.mapper.AdminCastingMapper;
import com.padimasso.autocasting.application.admin.repository.specification.AdminCastingSpecs;
import com.padimasso.autocasting.application.admin.service.AdminCastingService;
import com.padimasso.autocasting.application.castings.dto.response.CastingRoleResponse;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.repository.specification.CastingSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.CASTINGS_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AdminCastingServiceImpl implements AdminCastingService {

    private final CastingRepository castingRepository;
    private final CastingRoleRepository castingRoleRepository;
    private final AdminCastingMapper adminCastingMapper;

    @Override
    public AdminCastingsPageResponse listCastings(int page, int size, String q, List<String> statusIdTokens) {
        int normalizedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int normalizedPage = Math.max(page, 0);

        var pageable = PageRequest.of(
            normalizedPage,
            normalizedSize,
            Sort.by(Sort.Direction.DESC, "modifiedAt", "id")
        );

        var spec = AdminCastingSpecs.fromSearchText(q);
        if (statusIdTokens != null && !statusIdTokens.isEmpty()) {
            spec = spec.and(CastingSpecs.statusInTokens(statusIdTokens));
        }

        var result = castingRepository.findAll(spec, pageable);

        var items = result.getContent().stream()
            .map(adminCastingMapper::toRowResponse)
            .toList();

        return new AdminCastingsPageResponse(
            items,
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.hasNext()
        );
    }

    @Override
    public AdminCastingDetailsResponse getCastingDetailsBySlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new IllegalArgumentException(CASTINGS_NOT_FOUND);
        }

        CastingEntity casting = castingRepository.findByDefaultCodeAndDeletedFalse(slug.trim())
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));

        return adminCastingMapper.toDetailsResponse(casting);
    }

    @Override
    public CastingRoleResponse getCastingRoleById(UUID roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException(CASTINGS_NOT_FOUND);
        }

        CastingRoleEntity role = castingRoleRepository.findByIdAndDeletedFalse(roleId)
            .orElseThrow(() -> new IllegalArgumentException(CASTINGS_NOT_FOUND));

        return adminCastingMapper.toRoleResponse(role);
    }
}
