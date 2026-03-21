package com.padimasso.autocasting.application.applications.repository.order;

import com.padimasso.autocasting.application.shared.sort.AuditableOrderBy;
import com.padimasso.autocasting.application.shared.sort.OrderBySpec;
import org.springframework.data.domain.Sort;

public enum TalentCastingApplicationsOrderBy implements OrderBySpec {

    // Auditable
    CREATION_DATE_DESC(AuditableOrderBy.CREATED_DESC),
    CREATION_DATE_ASC(AuditableOrderBy.CREATED_ASC),
    MODIFIED_DATE_DESC(AuditableOrderBy.MODIFIED_DESC),
    MODIFIED_DATE_ASC(AuditableOrderBy.MODIFIED_ASC);

    private final Sort sort;

    TalentCastingApplicationsOrderBy(AuditableOrderBy base) {
        this.sort = base.toSort();
    }

    @Override
    public Sort toSort() {
        return sort;
    }
}
