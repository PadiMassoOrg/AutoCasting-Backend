package com.padimasso.autocasting.application.castings.service.impl;

import com.padimasso.autocasting.application.castings.dto.CastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.response.card.CastingRolePublicCardResponse;
import com.padimasso.autocasting.application.castings.mapper.CastingMapper;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.castings.repository.CastingRoleRepository;
import com.padimasso.autocasting.application.castings.repository.specification.CastingRoleSpecs;
import com.padimasso.autocasting.application.castings.service.CastingRoleSearchService;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;
import static com.padimasso.autocasting.config.AppConstants.MAX_PAGE_SIZE;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CastingRoleSearchServiceImpl implements CastingRoleSearchService {

    private final CastingRoleRepository castingRoleRepository;
    private final SiteMetadataResolver siteMetadataResolver;
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

        CastingStatusOptionEntity publishedStatus =
            siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_PUBLISHED);

        var spec = CastingRoleSpecs.fromFilter(filter, publishedStatus.getId());

        // 1) Página liviana
        var result = castingRoleRepository.findAll(spec, pageable);

        var ids = result.getContent().stream()
            .map(CastingRoleEntity::getId)
            .toList();

        if (ids.isEmpty()) {
            return new SliceResponse<>(List.of(), result.hasNext(), page, ps);
        }

        // 2) Rehidratación de solo los elementos de la página
        var hydrated = castingRoleRepository.findAllPublicCardsByIdIn(ids);

        var byId = hydrated.stream()
            .collect(java.util.stream.Collectors.toMap(
                CastingRoleEntity::getId,
                java.util.function.Function.identity()
            ));

        // 3) Preservar orden original
        List<CastingRolePublicCardResponse> items = ids.stream()
            .map(byId::get)
            .filter(java.util.Objects::nonNull)
            .map(castingMapper::toPublicRoleCardResponse)
            .toList();

        return new SliceResponse<>(items, result.hasNext(), page, ps);
    }
}
