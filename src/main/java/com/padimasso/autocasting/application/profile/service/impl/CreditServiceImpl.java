package com.padimasso.autocasting.application.profile.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.profile.dto.request.CreditRequest;
import com.padimasso.autocasting.application.profile.dto.response.CreditResponse;
import com.padimasso.autocasting.application.profile.mapper.ProfileMapper;
import com.padimasso.autocasting.application.profile.model.CreditEntity;
import com.padimasso.autocasting.application.profile.model.ProfileEntity;
import com.padimasso.autocasting.application.profile.repository.CreditRepository;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
import com.padimasso.autocasting.application.profile.service.CreditService;
import com.padimasso.autocasting.application.sitemetadata.repository.ProductionTypeRepository;
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
    private final ProfileRepository profileRepository;
    private final CreditRepository creditRepository;
    private final ProductionTypeRepository productionTypeRepository;
    private final ProfileMapper profileMapper;

    @Override
    @Transactional
    public CreditResponse createCredit(CreditRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        var foundProfile = profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
        var foundProductionType = productionTypeRepository.findById(request.productionTypeId())
            .orElseThrow(() -> new IllegalArgumentException("sitemetadata.production_type_not_found"));

        var newCredit = CreditEntity.builder()
            .productionType(foundProductionType)
            .projectName(request.projectName())
            .producerName(request.producerName())
            .role(request.role())
            .year(request.year())
            .profile(foundProfile)
            .build();

        return profileMapper.toCreditResponse(creditRepository.save(newCredit));
    }

    @Override
    public List<CreditResponse> listMyCredits() {
        ProfileEntity profile = getMyProfileOrThrow();

        return creditRepository.findAllByProfileId(profile.getId())
            .stream()
            .map(profileMapper::toCreditResponse)
            .toList();
    }

    @Override
    public CreditResponse getMyCredit(UUID id) {
        CreditEntity credit = getOwnedCreditOrThrow(id);
        return profileMapper.toCreditResponse(credit);
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

        return profileMapper.toCreditResponse(creditRepository.save(credit));
    }

    @Override
    @Transactional
    public void deleteMyCredit(UUID id) {
        CreditEntity credit = getOwnedCreditOrThrow(id);
        creditRepository.delete(credit);
    }


    /* ---------------- Helpers ---------------- */

    private ProfileEntity getMyProfileOrThrow() {
        UserEntity user = authContext.getCurrentUserOrThrow();
        return profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
    }

    private CreditEntity getOwnedCreditOrThrow(UUID creditId) {
        ProfileEntity myProfile = getMyProfileOrThrow();
        CreditEntity credit = creditRepository.findById(creditId)
            .orElseThrow(() -> new IllegalArgumentException("credit.not_found"));

        if (!credit.getProfile().getId().equals(myProfile.getId())) {
            throw new IllegalArgumentException("credit.forbidden");
        }

        return credit;
    }
}
