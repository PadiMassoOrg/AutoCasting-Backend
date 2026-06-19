package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.response.AdminCastingRowResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminCastingsPageResponse;
import com.padimasso.autocasting.application.admin.repository.specification.AdminCastingSpecs;
import com.padimasso.autocasting.application.admin.service.AdminCastingService;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.castings.repository.specification.CastingSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;

@Service
@RequiredArgsConstructor
public class AdminCastingServiceImpl implements AdminCastingService {

    private final CastingRepository castingRepository;

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
            .map(this::toRowResponse)
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

    private AdminCastingRowResponse toRowResponse(CastingEntity casting) {
        var employerProfile = casting.getEmployerProfile();
        var employerBasicInfo = employerProfile != null ? employerProfile.getBasicInfo() : null;

        return new AdminCastingRowResponse(
            casting.getId(),
            casting.getDefaultCode(),
            employerBasicInfo != null ? employerBasicInfo.getCompanyName() : null,
            casting.getTitle(),
            TalentProfileMapper.mapToSiteMetadataObject(casting.getStatus()),
            casting.getApplicationDeadline(),
            casting.getCreatedAt(),
            casting.getCreatedBy(),
            casting.getModifiedAt(),
            casting.getModifiedBy(),
            casting.isDeleted()
        );
    }
}
