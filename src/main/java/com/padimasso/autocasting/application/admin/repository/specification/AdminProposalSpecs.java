package com.padimasso.autocasting.application.admin.repository.specification;

import com.padimasso.autocasting.application.proposal.model.ProposalEntity;
import com.padimasso.autocasting.application.proposal.model.ProposalStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class AdminProposalSpecs {

    private AdminProposalSpecs() {
    }

    // The admin list only shows proposals still waiting to be claimed; CLAIMED/REVOKED are history.
    public static Specification<ProposalEntity> pendingOfTypes(List<UUID> typeIds) {
        Specification<ProposalEntity> spec = (root, query, cb) -> cb.and(
            cb.isFalse(root.get("deleted")),
            cb.equal(root.get("status"), ProposalStatus.PENDING)
        );

        if (typeIds == null || typeIds.isEmpty()) return spec;

        return spec.and((root, query, cb) -> root.get("type").get("id").in(typeIds));
    }

    // Free text over the internal reference: contact name, email, WhatsApp and company hint.
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
