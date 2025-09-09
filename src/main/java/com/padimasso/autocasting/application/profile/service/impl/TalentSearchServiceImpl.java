package com.padimasso.autocasting.application.profile.service.impl;

import com.padimasso.autocasting.application.profile.dao.ProfileSearchDao;
import com.padimasso.autocasting.application.profile.dto.TalentFilter;
import com.padimasso.autocasting.application.profile.dto.response.ProfileCardResponse;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
import com.padimasso.autocasting.application.profile.repository.projection.ProfessionRow;
import com.padimasso.autocasting.application.profile.repository.projection.ProfileCardRow;
import com.padimasso.autocasting.application.profile.service.TalentSearchService;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class TalentSearchServiceImpl implements TalentSearchService {
    private static final int MAX_PAGE_SIZE = 50;

    private final ProfileRepository profileRepository;
    private final ProfileSearchDao profileSearchDao;

    @Override
    @Transactional
    public SliceResponse<ProfileCardResponse> search(TalentFilter f, int page, int size) {
        var idSlice = profileSearchDao.searchIds(f, page, size);
        var ids = idSlice.ids();
        if (ids.isEmpty()) return new SliceResponse<>(List.of(), false, page, size);

        // detalles mínimos de la card
        var rows = profileRepository.findCardRowsByIds(ids);

        // profesiones
        var profRows = profileRepository.findProfessionsForProfiles(ids);
        var profByProfile = profRows.stream().collect(
            Collectors.groupingBy(
                ProfessionRow::getProfileId,
                Collectors.mapping(r -> new SiteMetadataObject(r.getId(), r.getStringCode(), null), Collectors.toList())
            )
        );

        var byId = rows.stream().collect(Collectors.toMap(ProfileCardRow::getId, r -> r));
        var items = new ArrayList<ProfileCardResponse>(ids.size());
        for (UUID id : ids) {
            var r = byId.get(id);
            if (r == null) continue;
            String publicSlug = r.getAllowsCustomSlug() != null && r.getAllowsCustomSlug()
                && r.getPremiumSlug() != null && !r.getPremiumSlug().isBlank()
                ? r.getPremiumSlug() : r.getDefaultSlug();
            items.add(new ProfileCardResponse(
                r.getId(), publicSlug, r.getStageName(), r.getEmail(), r.getPhoneNumber(), r.getHeadshotImageUrl(),
                profByProfile.getOrDefault(r.getId(), List.of())
            ));
        }

        return new SliceResponse<>(items, idSlice.hasNext(), page, size);
    }
}
