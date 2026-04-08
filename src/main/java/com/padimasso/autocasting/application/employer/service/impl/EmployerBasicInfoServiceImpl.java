package com.padimasso.autocasting.application.employer.service.impl;

import com.padimasso.autocasting.application.auth.context.EmployerContext;
import com.padimasso.autocasting.application.employer.dto.request.EmployerBasicInfoPatchRequest;
import com.padimasso.autocasting.application.employer.dto.response.EmployerBasicInfoResponse;
import com.padimasso.autocasting.application.employer.mapper.EmployerProfileMapper;
import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerBasicInfoRepository;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.employer.service.EmployerBasicInfoService;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataResolver;
import com.padimasso.autocasting.application.shared.util.TextNormalizer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class EmployerBasicInfoServiceImpl implements EmployerBasicInfoService {

    private final EmployerContext employerContext;
    private final EmployerProfileRepository employerProfileRepository;
    private final EmployerBasicInfoRepository employerBasicInfoRepository;
    private final SiteMetadataResolver siteMetadataResolver;
    private final EmployerProfileMapper employerProfileMapper;

    @Transactional
    @Override
    public EmployerBasicInfoResponse patchMyBasicInfo(EmployerBasicInfoPatchRequest req) {
        var principal = employerContext.getCurrentEmployerOrThrow();
        EmployerProfileEntity profile = principal.employerProfile();
        EmployerBasicInfoEntity basicInfo = employerBasicInfoRepository
                .findByEmployerProfileId(profile.getId())
                .orElseGet(() -> employerBasicInfoRepository.save(
                        EmployerBasicInfoEntity.builder()
                                .employerProfile(profile)
                                .build()
                ));

        if (req.companyTypeId() != null) {
            basicInfo.setCompanyType(siteMetadataResolver.resolveCompanyTypeOrThrow(req.companyTypeId()));
        }
        if (req.companyName().isPresent()) basicInfo.setCompanyName(TextNormalizer.normalizeNullable(req.companyName().orElse(null)));
        if (req.taxNumber().isPresent()) basicInfo.setTaxNumber(TextNormalizer.normalizeNullable(req.taxNumber().orElse(null)));
        if (req.companyEmail().isPresent()) basicInfo.setCompanyEmail(TextNormalizer.normalizeNullable(req.companyEmail().orElse(null)));
        if (req.imageUrl().isPresent()) basicInfo.setImageUrl(TextNormalizer.normalizeNullable(req.imageUrl().orElse(null)));
        if (req.address().isPresent()) basicInfo.setAddress(TextNormalizer.normalizeNullable(req.address().orElse(null)));
        if (req.websiteUrl().isPresent()) basicInfo.setWebsiteUrl(TextNormalizer.normalizeNullable(req.websiteUrl().orElse(null)));
        if (req.about().isPresent()) basicInfo.setAbout(TextNormalizer.normalizeNullable(req.about().orElse(null)));

        return employerProfileMapper.toBasicInfoResponse(employerBasicInfoRepository.save(basicInfo));
    }
}
