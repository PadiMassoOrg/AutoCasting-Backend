package com.padimasso.autocasting.application.castings.repository.order;

import com.padimasso.autocasting.application.shared.sort.AuditableOrderBy;
import com.padimasso.autocasting.application.shared.sort.OrderBySpec;
import org.springframework.data.domain.Sort;

public enum EmployerCastingsOrderBy implements OrderBySpec {

    // Auditable
    CREATION_DATE_DESC(AuditableOrderBy.CREATED_DESC),
    CREATION_DATE_ASC(AuditableOrderBy.CREATED_ASC),
    MODIFIED_DATE_DESC(AuditableOrderBy.MODIFIED_DESC),
    MODIFIED_DATE_ASC(AuditableOrderBy.MODIFIED_ASC),

    // Domain
    DEADLINE_ASC(Sort.unsorted()),
    DEADLINE_DESC(Sort.unsorted());

    private final Sort sort;

    EmployerCastingsOrderBy(AuditableOrderBy base) {
        this.sort = base.toSort();
    }

    EmployerCastingsOrderBy(Sort sort) {
        this.sort = sort;
    }

    @Override
    public Sort toSort() {
        return sort;
    }

    public boolean isDeadlineOrder() {
        return this == DEADLINE_ASC || this == DEADLINE_DESC;
    }
}
