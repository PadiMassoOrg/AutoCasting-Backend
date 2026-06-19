package com.padimasso.autocasting.application.admin.repository.specification;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class AdminUserSpecs {

    private AdminUserSpecs() {
    }

    public static Specification<UserEntity> fromSearchText(String raw) {
        String q = safeTrim(raw);
        if (q == null) return null;

        Specification<UserEntity> spec = Specification.where(emailContains(q));
        spec = or(spec, employerCompanyNameContains(q));
        spec = or(spec, talentStageNameContains(q));
        return spec;
    }

    private static Specification<UserEntity> emailContains(String raw) {
        String like = toLikePattern(raw);
        return (root, query, cb) -> cb.like(cb.lower(root.get("email")), like, '\\');
    }

    private static Specification<UserEntity> employerCompanyNameContains(String raw) {
        String like = toLikePattern(raw);
        return (root, query, cb) -> {
            var exists = query.subquery(Integer.class);
            var employer = exists.from(EmployerProfileEntity.class);
            var basicInfo = employer.join("basicInfo", JoinType.LEFT);

            exists.select(cb.literal(1));
            exists.where(
                cb.equal(employer.get("user"), root),
                cb.like(cb.lower(basicInfo.get("companyName")), like, '\\')
            );

            return cb.exists(exists);
        };
    }

    private static Specification<UserEntity> talentStageNameContains(String raw) {
        String like = toLikePattern(raw);
        return (root, query, cb) -> {
            var exists = query.subquery(Integer.class);
            var talent = exists.from(TalentProfileEntity.class);
            var basicInfo = talent.join("basicInfo", JoinType.LEFT);

            exists.select(cb.literal(1));
            exists.where(
                cb.equal(talent.get("user"), root),
                cb.like(cb.lower(basicInfo.get("stageName")), like, '\\')
            );

            return cb.exists(exists);
        };
    }

    private static Specification<UserEntity> or(Specification<UserEntity> base, Specification<UserEntity> next) {
        if (next == null) return base;
        return base == null ? next : base.or(next);
    }

    private static String safeTrim(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isBlank() ? null : t;
    }

    private static String toLikePattern(String token) {
        String s = token.toLowerCase(Locale.ROOT).trim();
        s = s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return "%" + s + "%";
    }
}
