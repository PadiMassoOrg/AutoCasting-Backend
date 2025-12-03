package com.padimasso.autocasting.application.employer.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.AuthContext;
import com.padimasso.autocasting.application.employer.dto.request.EmployerBasicInfoPatchRequest;
import com.padimasso.autocasting.application.employer.dto.response.EmployerBasicInfoResponse;
import com.padimasso.autocasting.application.employer.mapper.EmployerProfileMapper;
import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerBasicInfoRepository;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.employer.service.EmployerBasicInfoService;
import com.padimasso.autocasting.application.sitemetadata.model.CompanyTypeOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.repository.CompanyTypeOptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class EmployerBasicInfoServiceImpl implements EmployerBasicInfoService {

    private final AuthContext authContext;
    private final EmployerProfileRepository employerProfileRepository;
    private final EmployerBasicInfoRepository employerBasicInfoRepository;
    private final CompanyTypeOptionRepository companyTypeOptionRepository;
    private final EmployerProfileMapper employerProfileMapper;

    @Transactional
    @Override
    public EmployerBasicInfoResponse patchMyBasicInfo(EmployerBasicInfoPatchRequest req) {
        UserEntity user = authContext.getCurrentUserOrThrow();
        EmployerProfileEntity profile = employerProfileRepository.findByUserId(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("profile.not_found"));
        EmployerBasicInfoEntity basicInfo = employerBasicInfoRepository.findByEmployerProfileId(profile.getId())
            .orElseGet(() -> employerBasicInfoRepository.save(EmployerBasicInfoEntity.builder().employerProfile(profile).build()));

        if (req.companyTypeId() != null) {
            CompanyTypeOptionEntity companyTypeEntity = companyTypeOptionRepository.findById(req.companyTypeId())
                .orElseThrow(() -> new IllegalArgumentException("sitemetadata.company_type.not_found"));
            basicInfo.setCompanyType(companyTypeEntity);
        }
        if (req.userName().isPresent()) basicInfo.setUserName(req.userName().orElse(null));
        if (req.companyName().isPresent()) basicInfo.setCompanyName(req.companyName().orElse(null));
        if (req.email().isPresent()) basicInfo.setEmail(req.email().orElse(null));
        if (req.imageUrl().isPresent()) basicInfo.setImageUrl(req.imageUrl().orElse(null));
        if (req.address().isPresent()) basicInfo.setAddress(req.address().orElse(null));
        if (req.website().isPresent()) basicInfo.setWebsite(req.website().orElse(null));
        if (req.about().isPresent()) basicInfo.setAbout(req.about().orElse(null));

        return employerProfileMapper.toBasicInfoResponse(employerBasicInfoRepository.save(basicInfo));
    }

}
