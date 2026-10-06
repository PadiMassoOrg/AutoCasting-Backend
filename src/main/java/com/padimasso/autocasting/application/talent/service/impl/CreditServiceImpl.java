package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.common.dto.LastModifiedResponse;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import com.padimasso.autocasting.application.talent.dto.request.CreditRequest;
import com.padimasso.autocasting.application.talent.dto.request.CreditUpsertRequest;
import com.padimasso.autocasting.application.talent.dto.response.CreditResponse;
import com.padimasso.autocasting.application.talent.mapper.TalentProfileMapper;
import com.padimasso.autocasting.application.talent.model.CreditEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.CreditRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.CreditService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CreditServiceImpl implements CreditService {

    private final AuthContext authContext;
    private final TalentProfileRepository talentProfileRepository;
    private final CreditRepository creditRepository;
    private final SiteMetadataResolver siteMetadataResolver;
    private final TalentProfileMapper talentProfileMapper;

    @Override
    @Transactional
    public CreditResponse createCredit(CreditRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        var foundProfile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));
        var foundProductionType = siteMetadataResolver.resolveProductionTypeOrThrow(request.productionTypeId());

        var newCredit = CreditEntity.builder()
            .productionType(foundProductionType)
            .projectName(TextNormalizer.normalizeNullable(request.projectName()))
            .producerName(TextNormalizer.normalizeNullable(request.producerName()))
            .role(TextNormalizer.normalizeNullable(request.role()))
            .year(TextNormalizer.normalizeNullable(request.year()))
            .talentProfile(foundProfile)
            .build();

        return talentProfileMapper.toCreditResponse(creditRepository.save(newCredit));
    }

    @Override
    public List<CreditResponse> listMyCredits() {
        TalentProfileEntity profile = getMyProfileOrThrow();

        return creditRepository.findAllByTalentProfileId(profile.getId())
            .stream()
            .map(talentProfileMapper::toCreditResponse)
            .toList();
    }

    @Override
    public CreditResponse getMyCredit(UUID id) {
        CreditEntity credit = getOwnedCreditOrThrow(id);
        return talentProfileMapper.toCreditResponse(credit);
    }

    @Override
    @Transactional
    public CreditResponse patchMyCredit(UUID id, CreditRequest request) {
        CreditEntity credit = getOwnedCreditOrThrow(id);

        if (request.productionTypeId() != null) {
            credit.setProductionType(siteMetadataResolver.resolveProductionTypeOrThrow(request.productionTypeId()));
        }

        if (request.projectName() != null) credit.setProjectName(TextNormalizer.normalizeNullable(request.projectName()));
        if (request.producerName() != null) credit.setProducerName(TextNormalizer.normalizeNullable(request.producerName()));
        if (request.role() != null) credit.setRole(TextNormalizer.normalizeNullable(request.role()));
        if (request.year() != null) credit.setYear(TextNormalizer.normalizeNullable(request.year()));

        return talentProfileMapper.toCreditResponse(creditRepository.save(credit));
    }

    @Override
    @Transactional
    public LastModifiedResponse deleteMyCredit(UUID id) {
        CreditEntity credit = getOwnedCreditOrThrow(id);
        UUID profileId = credit.getTalentProfile().getId();

        creditRepository.softDelete(credit);
        talentProfileRepository.touchModifiedAt(profileId);

        return new LastModifiedResponse(talentProfileRepository.findModifiedAtById(profileId));
    }

    @Override
    @Transactional
    public List<CreditResponse> replaceCredits(TalentProfileEntity profile, List<CreditUpsertRequest> items) {
        Map<UUID, CreditEntity> existingById = creditRepository.findAllByTalentProfileId(profile.getId())
            .stream()
            .collect(Collectors.toMap(CreditEntity::getId, Function.identity()));
        Set<UUID> keptIds = new HashSet<>();
        List<CreditEntity> result = new ArrayList<>();

        for (CreditUpsertRequest item : items) {
            CreditRequest data = item.credit();
            CreditEntity credit;
            if (item.id() == null) {
                credit = CreditEntity.builder().talentProfile(profile).build();
            } else {
                credit = existingById.get(item.id());
                if (credit == null) {
                    throw new IllegalArgumentException("credit.not_found");
                }
                keptIds.add(credit.getId());
            }
            credit.setProductionType(siteMetadataResolver.resolveProductionTypeOrThrow(data.productionTypeId()));
            credit.setProjectName(TextNormalizer.normalizeNullable(data.projectName()));
            credit.setProducerName(TextNormalizer.normalizeNullable(data.producerName()));
            credit.setRole(TextNormalizer.normalizeNullable(data.role()));
            credit.setYear(TextNormalizer.normalizeNullable(data.year()));
            result.add(creditRepository.save(credit));
        }

        existingById.values().stream()
            .filter(credit -> !keptIds.contains(credit.getId()))
            .forEach(creditRepository::softDelete);
        talentProfileRepository.touchModifiedAt(profile.getId());

        return result.stream().map(talentProfileMapper::toCreditResponse).toList();
    }

    /* ---------------- Helpers ---------------- */

    private TalentProfileEntity getMyProfileOrThrow() {
        UserEntity user = authContext.getCurrentUserOrThrow();
        return talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException(PROFILE_NOT_FOUND));
    }

    private CreditEntity getOwnedCreditOrThrow(UUID creditId) {
        TalentProfileEntity myProfile = getMyProfileOrThrow();
        CreditEntity credit = creditRepository.findById(creditId)
            .orElseThrow(() -> new IllegalArgumentException("credit.not_found"));

        if (!credit.getTalentProfile().getId().equals(myProfile.getId())) {
            throw new IllegalArgumentException("credit.forbidden");
        }

        return credit;
    }
}
