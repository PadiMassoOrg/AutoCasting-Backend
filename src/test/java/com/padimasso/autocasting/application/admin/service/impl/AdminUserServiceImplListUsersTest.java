package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.mapper.AdminProfileMapper;
import com.padimasso.autocasting.application.admin.mapper.AdminUserMapper;
import com.padimasso.autocasting.application.admin.model.AdminUserActivityEntity;
import com.padimasso.autocasting.application.admin.repository.AdminUserActivityRepository;
import com.padimasso.autocasting.application.admin.repository.order.AdminUsersOrderBy;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.history.service.HistoryService;
import com.padimasso.autocasting.application.talent.repository.MediaRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.MediaStorageService;
import com.padimasso.autocasting.application.talent.service.TalentWelcomeEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplListUsersTest {

    private static final LocalDateTime LAST_SAVED_AT = LocalDateTime.of(2026, 3, 5, 12, 0);

    @Mock
    private UserRepository userRepository;
    @Mock
    private TalentProfileRepository talentProfileRepository;
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private MediaStorageService mediaStorageService;
    @Mock
    private EmployerProfileRepository employerProfileRepository;
    @Mock
    private AdminUserActivityRepository adminUserActivityRepository;
    @Mock
    private AdminUserMapper adminUserMapper;
    @Mock
    private AdminProfileMapper adminProfileMapper;
    @Mock
    private HistoryService historyService;
    @Mock
    private TalentWelcomeEmailService talentWelcomeEmailService;

    private AdminUserServiceImpl service;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        service = new AdminUserServiceImpl(
            userRepository,
            talentProfileRepository,
            mediaRepository,
            mediaStorageService,
            employerProfileRepository,
            adminUserActivityRepository,
            adminUserMapper,
            adminProfileMapper,
            historyService,
            talentWelcomeEmailService
        );
        user = UserEntity.builder().id(UUID.randomUUID()).build();
    }

    @SuppressWarnings("unchecked")
    private Pageable listAndCapturePageable(AdminUsersOrderBy orderBy) {
        when(userRepository.findAllIncludingDeleted(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(user)));
        when(adminUserActivityRepository.findAllById(anyList())).thenReturn(List.of());
        when(talentProfileRepository.findAllByUserIdInForAdmin(anyList())).thenReturn(List.of());

        service.listUsers(0, 8, null, false, orderBy);

        var pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAllIncludingDeleted(any(Specification.class), pageable.capture());
        return pageable.getValue();
    }

    @Test
    @SuppressWarnings("unchecked")
    void listUsers_rowValuesComeFromTheActivityView() {
        when(userRepository.findAllIncludingDeleted(any(Specification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(user)));
        when(adminUserActivityRepository.findAllById(anyList())).thenReturn(List.of(
            new AdminUserActivityEntity(user.getId(), "Jane Doe", "Acme Studio", LAST_SAVED_AT)
        ));
        when(talentProfileRepository.findAllByUserIdInForAdmin(anyList())).thenReturn(List.of());

        service.listUsers(0, 8, null, false, AdminUsersOrderBy.CREATION_DATE_DESC);

        verify(adminUserMapper).toRowResponse(eq(user), eq("Acme Studio"), eq("Jane Doe"), isNull(), eq(LAST_SAVED_AT));
    }

    @Test
    void listUsers_userMissingFromView_hasNoActivityValues() {
        listAndCapturePageable(AdminUsersOrderBy.CREATION_DATE_DESC);

        verify(adminUserMapper).toRowResponse(eq(user), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void listUsers_withoutOrderBy_defaultsToNewestCreatedFirst() {
        var pageable = listAndCapturePageable(null);

        assertEquals(AdminUsersOrderBy.CREATION_DATE_DESC.toSort(), pageable.getSort());
    }

    @Test
    void listUsers_emailOrder_usesCaseInsensitivePageableSort() {
        var pageable = listAndCapturePageable(AdminUsersOrderBy.EMAIL_ASC);

        var order = pageable.getSort().getOrderFor("email");
        assertEquals(Sort.Direction.ASC, order.getDirection());
        assertTrue(order.isIgnoreCase());
    }

    @Test
    void listUsers_activityOrder_leavesPageableUnsortedForTheSpecification() {
        var pageable = listAndCapturePageable(AdminUsersOrderBy.LAST_SAVED_DESC);

        assertTrue(pageable.getSort().isUnsorted());
    }
}
