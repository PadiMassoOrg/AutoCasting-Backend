package com.padimasso.autocasting.application.admin.repository.specification;

import com.padimasso.autocasting.application.admin.model.AdminUserActivityEntity;
import com.padimasso.autocasting.application.admin.repository.order.AdminUsersOrderBy;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.specification.TalentProfileSpecs;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import org.hibernate.query.criteria.JpaRoot;
import org.hibernate.query.sqm.tree.SqmJoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

import static com.padimasso.autocasting.config.AppConstants.PROPOSALS_SYSTEM_USER_ID;

public final class AdminUserSpecs {

    private AdminUserSpecs() {
    }

    public static Specification<UserEntity> fromSearchText(String raw) {
        String q = safeTrim(raw);
        if (q == null) return null;

        Specification<UserEntity> spec = emailContains(q);
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

    // Only matches users who actually have a talent profile; a profile is "not visible" if it's
    // deleted, the user is suspended, or it's missing the headshot/full-body photos required for
    // catalog visibility (the common case) — the media check delegates to
    // TalentProfileSpecs.hasRequiredMediaPredicate, the single source of truth for that rule.
    // (deleted/suspended stay inline here as a direct reference to `root`, since this subquery's
    // `visibleTalent` is already correlated to `root` as its `user` — going through
    // TalentProfileSpecs.activeProfilePredicate would re-join `visibleTalent.user` redundantly.)
    public static Specification<UserEntity> notVisibleInTalentCatalog() {
        return (root, query, cb) -> {
            var talentExists = query.subquery(Integer.class);
            var talent = talentExists.from(TalentProfileEntity.class);
            talentExists.select(cb.literal(1));
            talentExists.where(cb.equal(talent.get("user"), root));

            var visible = query.subquery(Integer.class);
            var visibleTalent = visible.from(TalentProfileEntity.class);
            visible.select(cb.literal(1));
            visible.where(
                cb.equal(visibleTalent.get("user"), root),
                cb.isFalse(visibleTalent.get("deleted")),
                cb.isFalse(root.get("suspended")),
                TalentProfileSpecs.hasRequiredMediaPredicate(visibleTalent, cb)
            );

            return cb.and(cb.exists(talentExists), cb.not(cb.exists(visible)));
        };
    }

    // Sorts by a column of the admin_user_activity view, nulls last in both directions, then newest
    // user first. Skipped on count queries, where ordering is not allowed.
    public static Specification<UserEntity> orderByActivity(AdminUsersOrderBy orderBy) {
        return (root, query, cb) -> {
            if (!orderBy.isActivityOrder() || isCountQuery(query.getResultType())) {
                return cb.conjunction();
            }

            var activity = ((JpaRoot<UserEntity>) root).join(AdminUserActivityEntity.class, SqmJoinType.LEFT);
            activity.on(cb.equal(activity.get("userId"), root.get("id")));

            var column = orderBy.activityColumn();
            Expression<?> value = column.isText()
                ? cb.lower(activity.<String>get(column.attribute()))
                : activity.get(column.attribute());

            query.orderBy(
                cb.asc(cb.selectCase().when(cb.isNull(value), 1).otherwise(0)),
                orderBy.isAscending() ? cb.asc(value) : cb.desc(value),
                cb.desc(root.get("createdAt")),
                cb.desc(root.get("id"))
            );
            return cb.conjunction();
        };
    }

    private static boolean isCountQuery(Class<?> resultType) {
        return Long.class.equals(resultType) || long.class.equals(resultType);
    }

    public static Specification<UserEntity> excludingSystemUsers() {
        return (root, query, cb) -> cb.notEqual(root.get("id"), PROPOSALS_SYSTEM_USER_ID);
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
