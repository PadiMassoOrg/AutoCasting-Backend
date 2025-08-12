package com.padimasso.autocasting.application.sitemetadata.service.impl;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataResponse;
import com.padimasso.autocasting.application.sitemetadata.model.ColorOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.SiteMetadataBase;
import com.padimasso.autocasting.application.sitemetadata.repository.ColorOptionRepository;
import com.padimasso.autocasting.application.sitemetadata.repository.DietOptionRepository;
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
    private final ColorOptionRepository colorOptionRepository;
    private final DietOptionRepository dietOptionRepository;

    public SiteMetadataResponse getSiteMetadata() {
        var foundSkillEntities = skillRepository.findAll();
        var foundProfessionEntities = professionRepository.findAll();
        var foundColorOptionEntities = colorOptionRepository.findAll();
        var foundDietOptionEntities = dietOptionRepository.findAll();

        return new SiteMetadataResponse(
            mapToSiteMetadataObject(foundSkillEntities),
            mapToSiteMetadataObject(foundProfessionEntities),
            mapToSiteMetadataObject(foundColorOptionEntities),
            mapToSiteMetadataObject(foundDietOptionEntities)
        );
    }

    private <T extends SiteMetadataBase> List<SiteMetadataObject> mapToSiteMetadataObject(List<T> entities) {
        return entities.stream()
            .map(entity -> {
                String category = null;
                if (entity instanceof ColorOptionEntity colorEntity) {
                    category = colorEntity.getCategory();
                }
                return new SiteMetadataObject(entity.getId(), entity.getStringCode(), category);
            })
            .toList();
    }
}
