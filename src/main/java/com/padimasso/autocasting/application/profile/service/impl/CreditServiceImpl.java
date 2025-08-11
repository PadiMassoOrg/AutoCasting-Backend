package com.padimasso.autocasting.application.profile.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.profile.dto.request.CreditRequest;
import com.padimasso.autocasting.application.profile.dto.response.CreditResponse;
import com.padimasso.autocasting.application.profile.mapper.ProfileMapper;
import com.padimasso.autocasting.application.profile.model.CreditEntity;
import com.padimasso.autocasting.application.profile.repository.CreditRepository;
import com.padimasso.autocasting.application.profile.repository.ProfileRepository;
import com.padimasso.autocasting.application.profile.service.CreditService;
import com.padimasso.autocasting.application.sitemetadata.repository.ProductionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public CreditResponse createCredit(CreditRequest request) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        var foundProfile = profileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
        var foundProductionType = productionTypeRepository.findById(request.productionTypeId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));

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
}
