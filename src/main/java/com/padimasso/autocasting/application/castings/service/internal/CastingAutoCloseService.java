package com.padimasso.autocasting.application.castings.service.internal;

import com.padimasso.autocasting.application.castings.repository.CastingRepository;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_CLOSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_DRAFT;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PAUSED;
import static com.padimasso.autocasting.config.AppConstants.CASTING_STATUS_PUBLISHED;

@Service
@RequiredArgsConstructor
public class CastingAutoCloseService {

    private final CastingRepository castingRepository;
    private final SiteMetadataResolver siteMetadataResolver;

    @Transactional
    public int closeExpiredCastings(LocalDate today) {
        var closed = siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_CLOSED);
        var draft = siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_DRAFT);
        var published = siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_PUBLISHED);
        var paused = siteMetadataResolver.resolveCastingStatusByCodeOrThrow(CASTING_STATUS_PAUSED);

        return castingRepository.closeExpiredCastings(
            today,
            closed.getId(),
            List.of(draft.getId(), published.getId(), paused.getId())
        );
    }
}
