package com.padimasso.autocasting.application.applications.repository.specification;

import com.padimasso.autocasting.application.applications.dto.EmployerCastingApplicantsFilter;
import com.padimasso.autocasting.application.applications.dto.TalentCastingApplicationsFilter;
import com.padimasso.autocasting.application.applications.model.CastingApplicationEntity;
import com.padimasso.autocasting.application.castings.model.CastingEntity;
import com.padimasso.autocasting.application.castings.model.CastingRoleEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingApplicationStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingModalityOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.CastingStatusOptionEntity;
import com.padimasso.autocasting.application.sitemetadata.model.ProjectTypeOptionEntity;
import com.padimasso.autocasting.application.talent.model.BasicInfoEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

public final class CastingApplicationSpecs {

    private CastingApplicationSpecs() {
    }

    public static Specification<CastingApplicationEntity> fromTalentFilter(TalentCastingApplicationsFilter f) {
        Specification<CastingApplicationEntity> spec = null;
        spec = and(spec, deletedFalse());
        spec = and(spec, forTalentProfile(f.talentProfileId()));
        spec = and(spec, talentSearchText(f.search()));
        spec = and(spec, castingStatusInTokens(f.castingStatusIdTokens()));
        spec = and(spec, projectTypeInTokens(f.projectTypeIdTokens()));
        spec = and(spec, modalityInTokens(f.modalityIdTokens()));
        return spec;
    }

    public static Specification<CastingApplicationEntity> fromEmployerFilter(EmployerCastingApplicantsFilter f) {
        Specification<CastingApplicationEntity> spec = null;
        spec = and(spec, deletedFalse());
        spec = and(spec, forEmployerAndCastingSlug(f.employerProfileId(), f.castingSlug()));
        spec = and(spec, forCastingRoleId(f.castingRoleId()));
        spec = and(spec, employerSearchText(f.search()));
        spec = and(spec, applicationStatusInTokens(f.applicationStatusIdTokens()));
        spec = and(spec, professionIdIn(f.professionIds()));
        return spec;
    }

    public static Specification<CastingApplicationEntity> deletedFalse() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    public static Specification<CastingApplicationEntity> forTalentProfile(UUID talentProfileId) {
        if (talentProfileId == null) return null;
        return (root, query, cb) -> cb.equal(root.join("talentProfile").get("id"), talentProfileId);
    }

    public static Specification<CastingApplicationEntity> forEmployerAndCastingSlug(UUID employerProfileId, String castingSlug) {
        String slug = safeTrim(castingSlug);
        if (employerProfileId == null || slug == null) return null;

        return (root, query, cb) -> {
            Join<CastingApplicationEntity, CastingRoleEntity> role = joinOnce(root, "castingRole", JoinType.INNER);
            Join<CastingRoleEntity, CastingEntity> casting = role.join("casting", JoinType.INNER);

            return cb.and(
                cb.equal(casting.get("defaultCode"), slug),
                cb.equal(casting.join("employerProfile").get("id"), employerProfileId)
            );
        };
    }

    public static Specification<CastingApplicationEntity> forCastingRoleId(UUID castingRoleId) {
        if (castingRoleId == null) return null;
        return (root, query, cb) -> cb.equal(root.join("castingRole").get("id"), castingRoleId);
    }

    public static Specification<CastingApplicationEntity> talentSearchText(String raw) {
        String q = safeTrim(raw);
        if (q == null) return null;
        return (root, query, cb) -> {
            Join<CastingApplicationEntity, CastingRoleEntity> role = joinOnce(root, "castingRole", JoinType.INNER);
            Join<CastingRoleEntity, CastingEntity> casting = role.join("casting", JoinType.INNER);
            String like = toLikePattern(q);
            return cb.or(
                cb.like(cb.lower(role.get("roleName")), like, '\\'),
                cb.like(cb.lower(casting.get("title")), like, '\\')
            );
        };
    }

    public static Specification<CastingApplicationEntity> employerSearchText(String raw) {
        String q = safeTrim(raw);
        if (q == null) return null;

        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);

            Join<CastingApplicationEntity, TalentProfileEntity> tp = joinOnce(root, "talentProfile", JoinType.INNER);
            Join<TalentProfileEntity, BasicInfoEntity> tbi = tp.join("basicInfo", JoinType.LEFT);
            Join<CastingApplicationEntity, CastingRoleEntity> role = joinOnce(root, "castingRole", JoinType.INNER);
            Join<CastingRoleEntity, CastingEntity> casting = role.join("casting", JoinType.INNER);
            String like = toLikePattern(q);

            Expression<String> stageName = tbi.get("stageName").as(String.class);
            Expression<String> roleName = role.get("roleName").as(String.class);
            Expression<String> castingDescription = casting.get("description").as(String.class);
            Expression<String> roleDescription = role.get("description").as(String.class);

            return cb.or(
                cb.like(cb.lower(stageName), like, '\\'),
                cb.like(cb.lower(roleName), like, '\\'),
                cb.like(cb.lower(castingDescription), like, '\\'),
                cb.like(cb.lower(roleDescription), like, '\\')
            );
        };
    }

    public static Specification<CastingApplicationEntity> castingStatusInTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty() || containsNullToken(tokens)) return null;
        ParsedTokens parsed = parseUuidOrStringCodes(tokens);
        if (parsed.ids.isEmpty() && parsed.codes.isEmpty()) return null;

        return (root, query, cb) -> {
            Join<CastingApplicationEntity, CastingRoleEntity> role = joinOnce(root, "castingRole", JoinType.INNER);
            Join<CastingRoleEntity, CastingEntity> casting = role.join("casting", JoinType.INNER);
            Join<CastingEntity, CastingStatusOptionEntity> status = casting.join("status", JoinType.LEFT);

            if (!parsed.ids.isEmpty() && parsed.codes.isEmpty()) return status.get("id").in(parsed.ids);
            if (parsed.ids.isEmpty() && !parsed.codes.isEmpty()) return status.get("stringCode").in(parsed.codes);
            return cb.or(status.get("id").in(parsed.ids), status.get("stringCode").in(parsed.codes));
        };
    }

    public static Specification<CastingApplicationEntity> projectTypeInTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty() || containsNullToken(tokens)) return null;
        ParsedTokens parsed = parseUuidOrStringCodes(tokens);
        if (parsed.ids.isEmpty() && parsed.codes.isEmpty()) return null;

        return (root, query, cb) -> {
            Join<CastingApplicationEntity, CastingRoleEntity> role = joinOnce(root, "castingRole", JoinType.INNER);
            Join<CastingRoleEntity, CastingEntity> casting = role.join("casting", JoinType.INNER);
            Join<CastingEntity, ProjectTypeOptionEntity> pt = casting.join("projectType", JoinType.LEFT);

            if (!parsed.ids.isEmpty() && parsed.codes.isEmpty()) return pt.get("id").in(parsed.ids);
            if (parsed.ids.isEmpty() && !parsed.codes.isEmpty()) return pt.get("stringCode").in(parsed.codes);
            return cb.or(pt.get("id").in(parsed.ids), pt.get("stringCode").in(parsed.codes));
        };
    }

    public static Specification<CastingApplicationEntity> modalityInTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty() || containsNullToken(tokens)) return null;
        ParsedTokens parsed = parseUuidOrStringCodes(tokens);
        if (parsed.ids.isEmpty() && parsed.codes.isEmpty()) return null;

        return (root, query, cb) -> {
            Join<CastingApplicationEntity, CastingRoleEntity> role = joinOnce(root, "castingRole", JoinType.INNER);
            Join<CastingRoleEntity, CastingEntity> casting = role.join("casting", JoinType.INNER);
            Join<CastingEntity, CastingModalityOptionEntity> cm = casting.join("castingModality", JoinType.LEFT);

            if (!parsed.ids.isEmpty() && parsed.codes.isEmpty()) return cm.get("id").in(parsed.ids);
            if (parsed.ids.isEmpty() && !parsed.codes.isEmpty()) return cm.get("stringCode").in(parsed.codes);
            return cb.or(cm.get("id").in(parsed.ids), cm.get("stringCode").in(parsed.codes));
        };
    }

    public static Specification<CastingApplicationEntity> applicationStatusInTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty() || containsNullToken(tokens)) return null;
        ParsedTokens parsed = parseUuidOrStringCodes(tokens);
        if (parsed.ids.isEmpty() && parsed.codes.isEmpty()) return null;

        return (root, query, cb) -> {
            Join<CastingApplicationEntity, CastingApplicationStatusOptionEntity> st = root.join("status", JoinType.LEFT);

            if (!parsed.ids.isEmpty() && parsed.codes.isEmpty()) return st.get("id").in(parsed.ids);
            if (parsed.ids.isEmpty() && !parsed.codes.isEmpty()) return st.get("stringCode").in(parsed.codes);
            return cb.or(st.get("id").in(parsed.ids), st.get("stringCode").in(parsed.codes));
        };
    }

    public static Specification<CastingApplicationEntity> professionIdIn(List<UUID> professionIds) {
        if (professionIds == null || professionIds.isEmpty()) return null;

        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);

            Join<CastingApplicationEntity, TalentProfileEntity> tp = joinOnce(root, "talentProfile", JoinType.INNER);
            Join<TalentProfileEntity, BasicInfoEntity> bi = tp.join("basicInfo", JoinType.LEFT);
            var prof = bi.joinSet("professions", JoinType.LEFT);

            return prof.get("id").in(professionIds);
        };
    }

    private static Specification<CastingApplicationEntity> and(
        Specification<CastingApplicationEntity> base,
        Specification<CastingApplicationEntity> next
    ) {
        if (next == null) return base;
        return base == null ? next : base.and(next);
    }

    private static boolean containsNullToken(List<String> tokens) {
        return tokens.stream().filter(Objects::nonNull).anyMatch(t -> "NULL".equalsIgnoreCase(t.trim()));
    }

    private static ParsedTokens parseUuidOrStringCodes(List<String> tokens) {
        List<UUID> ids = new ArrayList<>();
        List<String> codes = new ArrayList<>();

        for (String raw : tokens) {
            if (raw == null) continue;
            String t = raw.trim();
            if (t.isEmpty() || "NULL".equalsIgnoreCase(t)) continue;

            try {
                ids.add(UUID.fromString(t));
            } catch (IllegalArgumentException ex) {
                codes.add(t);
            }
        }
        return new ParsedTokens(ids, codes);
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

    @SuppressWarnings("unchecked")
    private static <X, Y> Join<X, Y> joinOnce(jakarta.persistence.criteria.From<X, ?> from, String attr, JoinType type) {
        for (var j : from.getJoins()) {
            if (j.getAttribute() != null && attr.equals(j.getAttribute().getName())) return (Join<X, Y>) j;
        }
        return from.join(attr, type);
    }

    private record ParsedTokens(List<UUID> ids, List<String> codes) {
    }
}
