package com.padimasso.autocasting.application.shared.sort;

import org.springframework.data.domain.Sort;

public enum AuditableOrderBy implements OrderBySpec {
    CREATED_DESC(Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))),
    CREATED_ASC(Sort.by(Sort.Order.asc("createdAt"), Sort.Order.asc("id"))),
    MODIFIED_DESC(Sort.by(Sort.Order.desc("modifiedAt"), Sort.Order.desc("id"))),
    MODIFIED_ASC(Sort.by(Sort.Order.asc("modifiedAt"), Sort.Order.asc("id")));

    private final Sort sort;

    AuditableOrderBy(Sort sort) {
        this.sort = sort;
    }

    @Override
    public Sort toSort() {
        return sort;
    }
}
