package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.talent.dto.TalentFilter;
import com.padimasso.autocasting.application.talent.dto.response.TalentCardResponse;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.repository.specification.TalentProfileSpecs;
import com.padimasso.autocasting.application.talent.service.TalentSearchService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class TalentSearchServiceImpl implements TalentSearchService {
    private static final int MAX_PAGE_SIZE = 50;

    private final TalentProfileRepository talentProfileRepository;

    @Override
    @Transactional
    public SliceResponse<TalentCardResponse> search(TalentFilter f, int page, int size) {
        int ps = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        var pageable = PageRequest.of(page, ps, Sort.by(Sort.Direction.DESC, "modifiedAt", "id"));

        var spec = TalentProfileSpecs.fromFilter(f);

        var result = talentProfileRepository.findAll(spec, pageable); // @EntityGraph evita N+1
        var items = result.getContent().stream().map(p -> {
            String slug = p.getPlan().isAllowsCustomSlug()
                && p.getPremiumSlug() != null && !p.getPremiumSlug().isBlank()
                ? p.getPremiumSlug() : p.getDefaultSlug();

            var profs = p.getBasicInfo().getProfessions().stream()
                .map(sm -> new SiteMetadataObject(sm.getId(), sm.getStringCode(), null))
                .toList();

            return new TalentCardResponse(
                p.getId(), slug,
                p.getBasicInfo().getStageName(),
                p.getContact().getEmail(),
                p.getContact().getPhoneNumber(),
                p.getMedia() != null ? p.getMedia().getHeadshotImageUrl() : null,
                profs
            );
        }).toList();

        return new SliceResponse<>(items, result.hasNext(), page, ps);
    }
}
