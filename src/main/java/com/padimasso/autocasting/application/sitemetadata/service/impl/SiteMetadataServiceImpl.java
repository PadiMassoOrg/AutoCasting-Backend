package com.padimasso.autocasting.application.sitemetadata.service.impl;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataResponse;
import com.padimasso.autocasting.application.sitemetadata.model.SiteMetadataBase;
import com.padimasso.autocasting.application.sitemetadata.repository.ProfessionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.SkillRepository;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteMetadataServiceImpl implements SiteMetadataService {
    private final SkillRepository skillRepository;
    private final ProfessionRepository professionRepository;

    public SiteMetadataResponse getSiteMetadata() {
        var foundSkillEntities = skillRepository.findAllByIsActiveTrue();
        var foundProfessionEntities = professionRepository.findAllByIsActiveTrue();

        return new SiteMetadataResponse(
            mapToSiteMetadataObject(foundSkillEntities),
            mapToSiteMetadataObject(foundProfessionEntities)
        );
    }

    private <T extends SiteMetadataBase> List<SiteMetadataObject> mapToSiteMetadataObject(List<T> entities) {
        return entities.stream()
            .map(entity -> new SiteMetadataObject(entity.getId(), entity.getStringCode()))
            .toList();
    }
}
