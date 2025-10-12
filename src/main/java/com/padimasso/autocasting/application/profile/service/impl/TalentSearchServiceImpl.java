package com.padimasso.autocasting.application.profile.service.impl;

import com.padimasso.autocasting.application.profile.dto.TalentFilter;
import com.padimasso.autocasting.application.profile.dto.response.ProfileCardResponse;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
import com.padimasso.autocasting.application.profile.repository.specification.ProfileSpecs;
import com.padimasso.autocasting.application.profile.service.TalentSearchService;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
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

    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public SliceResponse<ProfileCardResponse> search(TalentFilter f, int page, int size) {
        int ps = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        var pageable = PageRequest.of(page, ps, Sort.by(Sort.Direction.DESC, "modifiedAt", "id"));

        var spec = ProfileSpecs.fromFilter(f);

        var result = profileRepository.findAll(spec, pageable); // @EntityGraph evita N+1
        var items = result.getContent().stream().map(p -> {
            String slug = p.getPlan().isAllowsCustomSlug()
                && p.getPremiumSlug() != null && !p.getPremiumSlug().isBlank()
                ? p.getPremiumSlug() : p.getDefaultSlug();

            var profs = p.getBasicInfo().getProfessions().stream()
                .map(sm -> new SiteMetadataObject(sm.getId(), sm.getStringCode(), null))
                .toList();

            return new ProfileCardResponse(
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
