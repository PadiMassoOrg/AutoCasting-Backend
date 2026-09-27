package com.padimasso.autocasting.application.admin.repository.order;

import com.padimasso.autocasting.application.shared.sort.AuditableOrderBy;
import com.padimasso.autocasting.application.shared.sort.OrderBySpec;
import org.springframework.data.domain.Sort;

public enum AdminProposalsOrderBy implements OrderBySpec {

    // Auditable
    MODIFIED_DATE_DESC(AuditableOrderBy.MODIFIED_DESC),
    MODIFIED_DATE_ASC(AuditableOrderBy.MODIFIED_ASC);

    private final Sort sort;

    AdminProposalsOrderBy(AuditableOrderBy base) {
        this.sort = base.toSort();
    }

    @Override
    public Sort toSort() {
        return sort;
    }
}
