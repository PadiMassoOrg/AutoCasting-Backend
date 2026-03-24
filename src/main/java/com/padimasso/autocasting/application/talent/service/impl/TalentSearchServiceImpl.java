package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.talent.dto.TalentFilter;
import com.padimasso.autocasting.application.talent.dto.response.TalentCardResponse;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.repository.specification.TalentProfileSpecs;
import com.padimasso.autocasting.application.talent.service.TalentSearchService;
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
public class TalentSearchServiceImpl implements TalentSearchService {

    private final TalentProfileRepository talentProfileRepository;

    @Override
    @Transactional
    public SliceResponse<TalentCardResponse> search(TalentFilter f, int page, int size) {
        int ps = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        var pageable = PageRequest.of(page, ps, Sort.by(Sort.Direction.DESC, "modifiedAt", "id"));

        var spec = TalentProfileSpecs.fromFilter(f);

        // 1) Página liviana
        var result = talentProfileRepository.findAll(spec, pageable);

        var ids = result.getContent().stream()
            .map(TalentProfileEntity::getId)
            .toList();

        if (ids.isEmpty()) {
            return new SliceResponse<>(List.of(), result.hasNext(), page, ps);
        }

        // 2) Rehidratación con grafo necesario para la card
        var hydrated = talentProfileRepository.findAllForTalentCardsByIdIn(ids);

        var byId = hydrated.stream()
            .collect(java.util.stream.Collectors.toMap(
                TalentProfileEntity::getId,
                java.util.function.Function.identity()
            ));

        // 3) Preservar orden original
        var items = ids.stream()
            .map(byId::get)
            .filter(java.util.Objects::nonNull)
            .map(p -> {
                String slug = p.getPlan().isAllowsCustomSlug()
                    && p.getPremiumSlug() != null && !p.getPremiumSlug().isBlank()
                    ? p.getPremiumSlug()
                    : p.getDefaultSlug();

                var profs = p.getBasicInfo().getProfessions().stream()
                    .map(sm -> new SiteMetadataObject(sm.getId(), sm.getStringCode(), null))
                    .toList();

                return new TalentCardResponse(
                    p.getId(),
                    slug,
                    p.getBasicInfo().getStageName(),
                    p.getContact().getEmail(),
                    p.getContact().getPhoneNumber(),
                    p.getMedia() != null ? p.getMedia().getHeadshotImageUrl() : null,
                    profs
                );
            })
            .toList();

        return new SliceResponse<>(items, result.hasNext(), page, ps);
    }
}
