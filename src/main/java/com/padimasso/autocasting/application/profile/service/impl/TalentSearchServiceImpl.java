package com.padimasso.autocasting.application.profile.service.impl;

import com.padimasso.autocasting.application.profile.dto.response.ProfileCardResponse;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
import com.padimasso.autocasting.application.profile.repository.projection.ProfessionRow;
import com.padimasso.autocasting.application.profile.repository.projection.ProfileCardRow;
import com.padimasso.autocasting.application.profile.service.TalentSearchService;
import com.padimasso.autocasting.application.shared.web.SliceResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class TalentSearchServiceImpl implements TalentSearchService {
    private static final int MAX_PAGE_SIZE = 50;
    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public SliceResponse<ProfileCardResponse> listCards(int page, int size) {
        int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        // TODO - Specify Order of appearance
        Sort sort = Sort.by(
            Sort.Order.desc("modifiedAt"),
            Sort.Order.desc("id")
        );

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Slice<ProfileCardRow> slice = profileRepository.findCardRows(pageable);
        List<UUID> ids = slice.getContent().stream().map(ProfileCardRow::getId).toList();
        Map<UUID, List<SiteMetadataObject>> professionsByProfile;

        if (!ids.isEmpty()) {
            List<ProfessionRow> rows = profileRepository.findProfessionsForProfiles(ids);
            professionsByProfile = rows.stream().collect(
                Collectors.groupingBy(
                    ProfessionRow::getProfileId,
                    Collectors.mapping(
                        r -> new SiteMetadataObject(r.getId(), r.getStringCode(), null),
                        Collectors.toList()
                    )
                )
            );
        } else {
            professionsByProfile = Collections.emptyMap();
        }

        List<ProfileCardResponse> items = slice.getContent().stream()
            .map(r -> new ProfileCardResponse(
                r.getId(),
                r.getPublicSlug(),
                r.getStageName(),
                r.getEmail(),
                r.getPhoneNumber(),
                r.getHeadshotImageUrl(),
                professionsByProfile.getOrDefault(r.getId(), List.of())
            ))
            .toList();

        return new SliceResponse<>(items, slice.hasNext(), page, pageSize);
    }
}
