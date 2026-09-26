package com.padimasso.autocasting.application.admin.repository.specification;

import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class AdminProposalSpecs {

    private AdminProposalSpecs() {
    }

    private static final Set<ProposalStatus> LISTABLE_STATUSES = EnumSet.of(ProposalStatus.PENDING, ProposalStatus.CLAIMED);

    public static Set<ProposalStatus> listableStatuses(List<ProposalStatus> requested) {
        if (requested == null || requested.isEmpty()) return LISTABLE_STATUSES;

        Set<ProposalStatus> statuses = EnumSet.noneOf(ProposalStatus.class);
        requested.stream().filter(LISTABLE_STATUSES::contains).forEach(statuses::add);
        return statuses;
    }

    public static Specification<ProposalEntity> listable(List<UUID> typeIds, List<ProposalStatus> statuses) {
        Set<ProposalStatus> allowedStatuses = listableStatuses(statuses);
        Specification<ProposalEntity> spec = (root, query, cb) -> allowedStatuses.isEmpty()
            ? cb.disjunction()
            : cb.and(cb.isFalse(root.get("deleted")), root.get("status").in(allowedStatuses));

        if (typeIds == null || typeIds.isEmpty()) return spec;

        return spec.and((root, query, cb) -> root.get("type").get("id").in(typeIds));
    }

    public static Specification<ProposalEntity> fromSearchText(String q) {
        if (q == null || q.isBlank()) return null;

        String pattern = "%" + q.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) -> cb.or(
            cb.like(cb.lower(root.get("contactName")), pattern),
            cb.like(cb.lower(root.get("contactEmail")), pattern),
            cb.like(cb.lower(root.get("contactWhatsapp")), pattern),
            cb.like(cb.lower(root.get("companyNameHint")), pattern)
        );
    }
}
