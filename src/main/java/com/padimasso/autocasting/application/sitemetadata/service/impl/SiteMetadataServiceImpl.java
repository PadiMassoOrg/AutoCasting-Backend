package com.padimasso.autocasting.application.sitemetadata.service.impl;

import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataObject;
import com.padimasso.autocasting.application.sitemetadata.dto.response.SiteMetadataResponse;
import com.padimasso.autocasting.application.sitemetadata.dto.response.VersionResponse;
import com.padimasso.autocasting.application.sitemetadata.model.SiteMetadataBase;
import com.padimasso.autocasting.application.sitemetadata.repository.*;
import com.padimasso.autocasting.application.sitemetadata.service.SiteMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Formatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteMetadataServiceImpl implements SiteMetadataService {
    private final SkillRepository skillRepository;
    private final ProfessionRepository professionRepository;
    private final GenderOptionRepository genderOptionRepository;
    private final ColorOptionRepository colorOptionRepository;
    private final DietOptionRepository dietOptionRepository;
    private final ProductionTypeRepository productionTypeRepository;

    public SiteMetadataResponse getSiteMetadata() {
        var version = computeVersion();
        var foundSkillEntities = skillRepository.findAll();
        var foundProfessionEntities = professionRepository.findAll();
        var foundGenderOptionEntities = genderOptionRepository.findAll();
        var foundColorOptionEntities = colorOptionRepository.findAll();
        var foundDietOptionEntities = dietOptionRepository.findAll();
        var foundProductionTypeOptionEntities = productionTypeRepository.findAll();

        return new SiteMetadataResponse(
            version,
            mapToSiteMetadataObject(foundSkillEntities),
            mapToSiteMetadataObject(foundProfessionEntities),
            mapToSiteMetadataObject(foundGenderOptionEntities),
            mapToSiteMetadataObject(foundColorOptionEntities),
            mapToSiteMetadataObject(foundDietOptionEntities),
            mapToSiteMetadataObject(foundProductionTypeOptionEntities)
        );
    }

    @Override
    public VersionResponse getVersionOnly() {
        return new VersionResponse(computeVersion());
    }

    private <T extends SiteMetadataBase> List<SiteMetadataObject> mapToSiteMetadataObject(List<T> entities) {
        return entities.stream()
            .map(entity -> new SiteMetadataObject(
                entity.getId(),
                entity.getStringCode(),
                entity.getCategoryStringCode()
            ))
            .toList();
    }

    private String computeVersion() {
        String parts = String.join("|",
            "skills:" + skillRepository.count() + ":" + ts(skillRepository.findMaxModifiedAt()),
            "professions:" + professionRepository.count() + ":" + ts(professionRepository.findMaxModifiedAt()),
            "gender:" + genderOptionRepository.count() + ":" + ts(genderOptionRepository.findMaxModifiedAt()),
            "colors:" + colorOptionRepository.count() + ":" + ts(colorOptionRepository.findMaxModifiedAt()),
            "diet:" + dietOptionRepository.count() + ":" + ts(dietOptionRepository.findMaxModifiedAt()),
            "productionType:" + productionTypeRepository.count() + ":" + ts(productionTypeRepository.findMaxModifiedAt())
        );
        return sha256(parts);
    }

    private String ts(Instant i) {
        return i == null ? "0" : String.valueOf(i.toEpochMilli());
    }

    private String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(s.getBytes(StandardCharsets.UTF_8));
            try (Formatter formatter = new Formatter()) {
                for (byte b : hash) formatter.format("%02x", b);
                return formatter.toString();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot compute sitemetadata version", e);
        }
    }

}
