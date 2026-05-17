package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.dto.CastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRolePublicCardResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.repository.specification.CastingRoleSpecs;
import com.padimasso.autocasting.application.castings.service.CastingRoleSearchService;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingRoleSearchServiceImpl implements CastingRoleSearchService {

    private final CastingRoleRepository castingRoleRepository;
    private final CastingMapper castingMapper;

    @Override
    @Transactional
    public SliceResponse<CastingRolePublicCardResponse> search(CastingRoleFilter filter, int page, int size) {
        int ps = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        var pageable = PageRequest.of(
            page,
            ps,
            Sort.by(Sort.Direction.DESC, "createdAt", "id")
        );

        var result = castingRoleRepository.findAll(CastingRoleSpecs.fromFilter(filter), pageable);

        List<CastingRolePublicCardResponse> items = result.getContent().stream()
            .map(castingMapper::toPublicRoleCardResponse)
            .toList();

        return new SliceResponse<>(items, result.hasNext(), page, ps, result.getTotalElements());
    }
}
