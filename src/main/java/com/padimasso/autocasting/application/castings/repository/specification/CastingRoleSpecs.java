package com.padimasso.autocasting.application.castings.repository.specification;

import com.padimasso.autocasting.application.castings.dto.CastingRoleFilter;
import com.padimasso.autocasting.application.castings.dto.EmployerCastingRoleFilter;
import com.padimasso.autocasting.application.castings.model.*;
import com.padimasso.autocasting.application.sitemetadata.model.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CastingRoleSpecs {
    private CastingRoleSpecs() {
    }

    public static Specification<CastingRoleEntity> fromFilter(CastingRoleFilter f, UUID publishedStatusId) {
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);

            Join<CastingRoleEntity, CastingRolesSectionEntity> rolesSection =
                root.join("rolesSection", JoinType.INNER);
            Join<CastingRolesSectionEntity, CastingEntity> casting =
                rolesSection.join("casting", JoinType.INNER);
            Join<CastingEntity, CastingBasicInfoEntity> basicInfo =
                casting.join("basicInfo", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(casting.get("status").get("id"), publishedStatusId));
            predicates.add(cb.isFalse(casting.get("deleted")));
            predicates.add(cb.isFalse(root.get("deleted")));

            if (f.roleName() != null && !f.roleName().isBlank()) {
                String pattern = "%" + f.roleName().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("roleName")), pattern));
            }

            if (f.ageMin() != null) {
                predicates.add(cb.or(
                    cb.isNull(root.get("ageMax")),
                    cb.greaterThanOrEqualTo(root.get("ageMax"), f.ageMin().shortValue())
                ));
            }

            if (f.ageMax() != null) {
                predicates.add(cb.or(
                    cb.isNull(root.get("ageMin")),
                    cb.lessThanOrEqualTo(root.get("ageMin"), f.ageMax().shortValue())
                ));
            }

            if (f.genderIdTokens() != null && !f.genderIdTokens().isEmpty()) {
                Join<CastingRoleEntity, GenderOptionEntity> gender =
                    root.join("gender", JoinType.LEFT);
                var in = cb.in(gender.get("stringCode"));
                f.genderIdTokens().forEach(in::value);
                predicates.add(in);
            }

            Join<CastingRoleEntity, CastingRoleCharacteristicsEntity> characteristics = null;
            if ((f.ethnicityIdTokens() != null && !f.ethnicityIdTokens().isEmpty())
                || f.heightMinCm() != null
                || f.heightMaxCm() != null
                || f.hairColorIds() != null && !f.hairColorIds().isEmpty()
                || f.eyeColorIds() != null && !f.eyeColorIds().isEmpty()
                || f.tattoo() != null
                || f.passport() != null
                || f.drivingLicense() != null) {

                characteristics = root.join("characteristics", JoinType.LEFT);
            }

            if (characteristics != null && f.ethnicityIdTokens() != null && !f.ethnicityIdTokens().isEmpty()) {
                Join<CastingRoleCharacteristicsEntity, EthnicityOptionEntity> ethnicity =
                    characteristics.join("ethnicity", JoinType.LEFT);
                var in = cb.in(ethnicity.get("stringCode"));
                f.ethnicityIdTokens().forEach(in::value);
                predicates.add(in);
            }

            if (f.professionIds() != null && !f.professionIds().isEmpty()) {
                Join<CastingRoleEntity, ProfessionEntity> prof =
                    root.joinSet("professions", JoinType.LEFT);
                var in = cb.in(prof.get("id"));
                f.professionIds().forEach(in::value);
                predicates.add(in);
            }

            if (characteristics != null && f.heightMinCm() != null) {
                predicates.add(cb.greaterThanOrEqualTo(characteristics.get("heightCm"), f.heightMinCm()));
            }

            if (characteristics != null && f.heightMaxCm() != null) {
                predicates.add(cb.lessThanOrEqualTo(characteristics.get("heightCm"), f.heightMaxCm()));
            }

            if (characteristics != null && f.hairColorIds() != null && !f.hairColorIds().isEmpty()) {
                Join<CastingRoleCharacteristicsEntity, ColorOptionEntity> hairColor =
                    characteristics.join("hairColor", JoinType.LEFT);
                var in = cb.in(hairColor.get("id"));
                f.hairColorIds().forEach(in::value);
                predicates.add(in);
            }

            if (characteristics != null && f.eyeColorIds() != null && !f.eyeColorIds().isEmpty()) {
                Join<CastingRoleCharacteristicsEntity, ColorOptionEntity> eyeColor =
                    characteristics.join("eyeColor", JoinType.LEFT);
                var in = cb.in(eyeColor.get("id"));
                f.eyeColorIds().forEach(in::value);
                predicates.add(in);
            }

            if (characteristics != null && f.tattoo() != null) {
                predicates.add(cb.equal(characteristics.get("tattoo"), f.tattoo()));
            }

            if (characteristics != null && f.passport() != null) {
                predicates.add(cb.equal(characteristics.get("passport"), f.passport()));
            }

            if (characteristics != null && f.drivingLicense() != null) {
                predicates.add(cb.equal(characteristics.get("drivingLicense"), f.drivingLicense()));
            }

            if (f.skillIds() != null && !f.skillIds().isEmpty()) {
                Join<CastingRoleEntity, SkillEntity> skill =
                    root.joinSet("skills", JoinType.LEFT);
                var in = cb.in(skill.get("id"));
                f.skillIds().forEach(in::value);
                predicates.add(in);
            }

            if (f.projectTypeIds() != null && !f.projectTypeIds().isEmpty()) {
                Join<CastingBasicInfoEntity, ?> projectType =
                    basicInfo.join("projectType", JoinType.LEFT);
                var in = cb.in(projectType.get("id"));
                f.projectTypeIds().forEach(in::value);
                predicates.add(in);
            }

            if (f.castingModalityIds() != null && !f.castingModalityIds().isEmpty()) {
                Join<CastingBasicInfoEntity, ?> castingModality =
                    basicInfo.join("castingModality", JoinType.LEFT);
                var in = cb.in(castingModality.get("id"));
                f.castingModalityIds().forEach(in::value);
                predicates.add(in);
            }

            if (f.locationText() != null && !f.locationText().isBlank()) {
                String pattern = "%" + f.locationText().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(basicInfo.get("locationText")), pattern));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<CastingRoleEntity> byRolesSectionId(UUID sectionId) {
        if (sectionId == null) return null;
        return (root, query, cb) ->
            cb.equal(root.join("rolesSection").get("id"), sectionId);
    }

    public static Specification<CastingRoleEntity> fromEmployerFilter(EmployerCastingRoleFilter f) {
        Specification<CastingRoleEntity> spec = null;

        spec = and(spec, byRolesSectionId(f.rolesSectionId()));
        // TODO: Filtering

        return spec;
    }

    private static Specification<CastingRoleEntity> and(
        Specification<CastingRoleEntity> base,
        Specification<CastingRoleEntity> next
    ) {
        if (next == null) return base;
        return (base == null) ? next : base.and(next);
    }

}
