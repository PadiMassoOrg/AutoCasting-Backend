package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.sitemetadata.repository.ProductionTypeRepository;
import com.padimasso.autocasting.application.talent.dto.request.CreditRequest;
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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CreditServiceImpl implements CreditService {

    private final AuthContext authContext;
    private final TalentProfileRepository talentProfileRepository;
    private final CreditRepository creditRepository;
    private final ProductionTypeRepository productionTypeRepository;
    private final TalentProfileMapper talentProfileMapper;

    @Override
    @Transactional
    public CreditResponse createCredit(CreditRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        var foundProfile = talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
        var foundProductionType = productionTypeRepository.findById(request.productionTypeId())
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.production_type_not_found"));

        var newCredit = CreditEntity.builder()
            .productionType(foundProductionType)
            .projectName(request.projectName())
            .producerName(request.producerName())
            .role(request.role())
            .year(request.year())
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
            var prodType = productionTypeRepository.findById(request.productionTypeId())
                .orElseThrow(() -> new IllegalArgumentException("sitemetadata.production_type_not_found"));
            credit.setProductionType(prodType);
        }

        if (request.projectName() != null) credit.setProjectName(request.projectName());
        if (request.producerName() != null) credit.setProducerName(request.producerName());
        if (request.role() != null) credit.setRole(request.role());
        if (request.year() != null) credit.setYear(request.year());

        return talentProfileMapper.toCreditResponse(creditRepository.save(credit));
    }

    @Override
    @Transactional
    public void deleteMyCredit(UUID id) {
        CreditEntity credit = getOwnedCreditOrThrow(id);
        creditRepository.delete(credit);
    }


    /* ---------------- Helpers ---------------- */

    private TalentProfileEntity getMyProfileOrThrow() {
        UserEntity user = authContext.getCurrentUserOrThrow();
        return talentProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
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
