package com.padimasso.autocasting.application.admin.repository.order;

import com.padimasso.autocasting.application.shared.sort.AuditableOrderBy;
import com.padimasso.autocasting.application.shared.sort.OrderBySpec;
import org.springframework.data.domain.Sort;

public enum AdminUsersOrderBy implements OrderBySpec {

    // Auditable
    CREATION_DATE_DESC(AuditableOrderBy.CREATED_DESC.toSort()),
    CREATION_DATE_ASC(AuditableOrderBy.CREATED_ASC.toSort()),

    // User
    EMAIL_ASC(Sort.by(Sort.Order.asc("email").ignoreCase(), Sort.Order.asc("id"))),
    EMAIL_DESC(Sort.by(Sort.Order.desc("email").ignoreCase(), Sort.Order.desc("id"))),

    // Backed by the admin_user_activity view (see AdminUserSpecs.orderByActivity)
    LAST_SAVED_DESC(ActivityColumn.LAST_SAVED, false),
    LAST_SAVED_ASC(ActivityColumn.LAST_SAVED, true),
    STAGE_NAME_ASC(ActivityColumn.STAGE_NAME, true),
    STAGE_NAME_DESC(ActivityColumn.STAGE_NAME, false),
    COMPANY_NAME_ASC(ActivityColumn.COMPANY_NAME, true),
    COMPANY_NAME_DESC(ActivityColumn.COMPANY_NAME, false);

    public enum ActivityColumn {
        LAST_SAVED("lastSavedAt", false),
        STAGE_NAME("talentStageName", true),
        COMPANY_NAME("employerCompanyName", true);

        private final String attribute;
        private final boolean text;

        ActivityColumn(String attribute, boolean text) {
            this.attribute = attribute;
            this.text = text;
        }

        public String attribute() {
            return attribute;
        }

        public boolean isText() {
            return text;
        }
    }

    private final Sort sort;
    private final ActivityColumn activityColumn;
    private final boolean ascending;

    AdminUsersOrderBy(Sort sort) {
        this.sort = sort;
        this.activityColumn = null;
        this.ascending = false;
    }

    AdminUsersOrderBy(ActivityColumn activityColumn, boolean ascending) {
        this.sort = Sort.unsorted();
        this.activityColumn = activityColumn;
        this.ascending = ascending;
    }

    @Override
    public Sort toSort() {
        return sort;
    }

    public boolean isActivityOrder() {
        return activityColumn != null;
    }

    public ActivityColumn activityColumn() {
        return activityColumn;
    }

    public boolean isAscending() {
        return ascending;
    }
}
