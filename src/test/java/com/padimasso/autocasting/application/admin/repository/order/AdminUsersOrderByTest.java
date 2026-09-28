package com.padimasso.autocasting.application.admin.repository.order;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminUsersOrderByTest {

    @Test
    void creationDateOrders_sortByCreatedAtThenId() {
        assertEquals(Sort.Direction.DESC, AdminUsersOrderBy.CREATION_DATE_DESC.toSort().getOrderFor("createdAt").getDirection());
        assertEquals(Sort.Direction.ASC, AdminUsersOrderBy.CREATION_DATE_ASC.toSort().getOrderFor("createdAt").getDirection());
        assertFalse(AdminUsersOrderBy.CREATION_DATE_DESC.isActivityOrder());
    }

    @Test
    void emailOrders_ignoreCase() {
        assertTrue(AdminUsersOrderBy.EMAIL_ASC.toSort().getOrderFor("email").isIgnoreCase());
        assertEquals(Sort.Direction.DESC, AdminUsersOrderBy.EMAIL_DESC.toSort().getOrderFor("email").getDirection());
        assertNull(AdminUsersOrderBy.EMAIL_ASC.activityColumn());
    }

    @Test
    void lastSavedOrders_useTheViewTimestampColumn() {
        assertTrue(AdminUsersOrderBy.LAST_SAVED_DESC.isActivityOrder());
        assertEquals("lastSavedAt", AdminUsersOrderBy.LAST_SAVED_DESC.activityColumn().attribute());
        assertFalse(AdminUsersOrderBy.LAST_SAVED_DESC.activityColumn().isText());
        assertFalse(AdminUsersOrderBy.LAST_SAVED_DESC.isAscending());
        assertTrue(AdminUsersOrderBy.LAST_SAVED_ASC.isAscending());
        assertTrue(AdminUsersOrderBy.LAST_SAVED_DESC.toSort().isUnsorted());
    }

    @Test
    void nameOrders_useTheViewTextColumns() {
        assertEquals("talentStageName", AdminUsersOrderBy.STAGE_NAME_ASC.activityColumn().attribute());
        assertEquals("employerCompanyName", AdminUsersOrderBy.COMPANY_NAME_DESC.activityColumn().attribute());
        assertTrue(AdminUsersOrderBy.STAGE_NAME_ASC.activityColumn().isText());
        assertTrue(AdminUsersOrderBy.STAGE_NAME_ASC.isAscending());
        assertFalse(AdminUsersOrderBy.COMPANY_NAME_DESC.isAscending());
    }
}
