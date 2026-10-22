package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.request.AdminBulkEmailRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminBulkEmailResultResponse.SkipReason;
import com.padimasso.autocasting.application.admin.dto.response.AdminBulkEmailResultResponse.Skipped;
import com.padimasso.autocasting.application.admin.dto.response.AdminBulkEmailResultResponse;
import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.email.model.EmailAudience;
import com.padimasso.autocasting.application.email.model.EmailType;
import com.padimasso.autocasting.application.email.service.EmailDeliveryService;
import com.padimasso.autocasting.application.email.service.EmailPreferencesService;
import com.padimasso.autocasting.application.email.service.sender.BulkEmailRecipient;
import com.padimasso.autocasting.application.email.service.sender.BulkEmailSender;
import com.padimasso.autocasting.application.email.service.sender.BulkEmailSenderRegistry;
import com.padimasso.autocasting.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.padimasso.autocasting.config.AppConstants.EMAIL_RESEND_COOLDOWN_DAYS;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.GENERAL_UNEXPECTED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROFILE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBulkEmailServiceImplTest {

    private static final EmailType TYPE = EmailType.TALENT_WELCOME;

    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailDeliveryService emailDeliveryService;
    @Mock
    private EmailPreferencesService emailPreferencesService;
    @Mock
    private BulkEmailSenderRegistry bulkEmailSenderRegistry;
    @Mock
    private BulkEmailSender sender;

    private AdminBulkEmailServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AdminBulkEmailServiceImpl(userRepository, emailDeliveryService, emailPreferencesService, bulkEmailSenderRegistry);
        lenient().when(bulkEmailSenderRegistry.forType(TYPE)).thenReturn(sender);
        lenient().when(emailPreferencesService.ensureUnsubscribeToken(any())).thenAnswer(inv -> "token-" + ((UserEntity) inv.getArgument(0)).getId());
        lenient().when(sender.send(anyList())).thenReturn(Map.of());
    }

    private UserEntity talent() {
        return UserEntity.builder()
            .id(UUID.randomUUID())
            .talentOnboardingStatus(OnboardingStatus.COMPLETED)
            .employerOnboardingStatus(OnboardingStatus.NOT_STARTED)
            .build();
    }

    @SuppressWarnings("unchecked")
    private void givenUsers(UserEntity... users) {
        when(userRepository.findAllIncludingDeleted(any(Specification.class))).thenReturn(Arrays.asList(users));
    }

    private void givenLastSentAt(Map<UUID, LocalDateTime> lastSentAt) {
        when(emailDeliveryService.findLastSentAt(anyList(), any())).thenReturn(lastSentAt);
    }

    private AdminBulkEmailResultResponse send(boolean forceResend, UserEntity... users) {
        List<UUID> ids = Arrays.stream(users).map(UserEntity::getId).toList();
        return service.sendBulkEmail(new AdminBulkEmailRequest(TYPE, ids, forceResend));
    }

    @Test
    void listEmailTypes_returnsEveryTypeWithItsAudienceAndTheCooldown() {
        var response = service.listEmailTypes();

        assertEquals(EMAIL_RESEND_COOLDOWN_DAYS, response.resendCooldownDays());
        assertEquals(EmailAudience.TALENT, response.types().stream()
            .filter(item -> item.type() == EmailType.TALENT_WELCOME).findFirst().orElseThrow().audience());
    }

    @Test
    void sendBulkEmail_eligibleUser_isSentAndRecorded() {
        UserEntity user = talent();
        givenUsers(user);
        givenLastSentAt(Map.of());

        var result = send(false, user);

        assertEquals(1, result.sentCount());
        ArgumentCaptor<List<BulkEmailRecipient>> recipients = ArgumentCaptor.captor();
        verify(sender).send(recipients.capture());
        assertEquals("token-" + user.getId(), recipients.getValue().getFirst().unsubscribeToken());
        verify(emailDeliveryService).recordAll(List.of(user.getId()), TYPE);
    }

    @Test
    void sendBulkEmail_suspendedOrDeletedUser_isSkippedAsInactive() {
        UserEntity suspended = talent();
        suspended.setSuspended(true);
        UserEntity deleted = talent();
        deleted.setDeleted(true);
        givenUsers(suspended, deleted);
        givenLastSentAt(Map.of());

        var result = send(false, suspended, deleted);

        assertEquals(List.of(
            new Skipped(suspended.getId(), SkipReason.USER_INACTIVE),
            new Skipped(deleted.getId(), SkipReason.USER_INACTIVE)
        ), result.skipped());
        verify(sender, never()).send(anyList());
    }

    @Test
    void sendBulkEmail_userOutsideAudience_isSkipped() {
        UserEntity employer = UserEntity.builder()
            .id(UUID.randomUUID())
            .talentOnboardingStatus(OnboardingStatus.NOT_STARTED)
            .employerOnboardingStatus(OnboardingStatus.COMPLETED)
            .build();
        givenUsers(employer);
        givenLastSentAt(Map.of());

        var result = send(false, employer);

        assertEquals(List.of(new Skipped(employer.getId(), SkipReason.AUDIENCE_MISMATCH)), result.skipped());
    }

    @Test
    void sendBulkEmail_unsubscribedUser_isSkippedEvenWhenForced() {
        UserEntity user = talent();
        user.setMarketingEmailsUnsubscribedAt(LocalDateTime.now().minusDays(1));
        givenUsers(user);
        givenLastSentAt(Map.of());

        var result = send(true, user);

        assertEquals(List.of(new Skipped(user.getId(), SkipReason.UNSUBSCRIBED)), result.skipped());
        verify(emailPreferencesService, never()).ensureUnsubscribeToken(any());
    }

    @Test
    void sendBulkEmail_recentlySent_isSkipped() {
        UserEntity user = talent();
        givenUsers(user);
        givenLastSentAt(Map.of(user.getId(), LocalDateTime.now().minusDays(EMAIL_RESEND_COOLDOWN_DAYS - 1)));

        var result = send(false, user);

        assertEquals(List.of(new Skipped(user.getId(), SkipReason.RECENTLY_SENT)), result.skipped());
        assertEquals(0, result.sentCount());
    }

    @Test
    void sendBulkEmail_recentlySentButForced_isSent() {
        UserEntity user = talent();
        givenUsers(user);
        givenLastSentAt(Map.of(user.getId(), LocalDateTime.now().minusDays(1)));

        var result = send(true, user);

        assertEquals(1, result.sentCount());
        assertEquals(0, result.skippedCount());
    }

    @Test
    void sendBulkEmail_sentBeforeTheCooldown_isSentAgain() {
        UserEntity user = talent();
        givenUsers(user);
        givenLastSentAt(Map.of(user.getId(), LocalDateTime.now().minusDays(EMAIL_RESEND_COOLDOWN_DAYS + 1)));

        var result = send(false, user);

        assertEquals(1, result.sentCount());
    }

    @Test
    void sendBulkEmail_senderFailure_isReportedAndNotRecorded() {
        UserEntity sent = talent();
        UserEntity failed = talent();
        givenUsers(sent, failed);
        givenLastSentAt(Map.of());
        when(sender.send(anyList())).thenReturn(Map.of(failed.getId(), GENERAL_UNEXPECTED));

        var result = send(false, sent, failed);

        assertEquals(1, result.sentCount());
        assertEquals(1, result.failedCount());
        assertEquals(GENERAL_UNEXPECTED, result.failures().getFirst().reason());
        verify(emailDeliveryService).recordAll(List.of(sent.getId()), TYPE);
    }

    @Test
    void sendBulkEmail_unknownUser_isReportedAsFailure() {
        UserEntity user = talent();
        UUID unknownId = UUID.randomUUID();
        givenUsers(user);
        givenLastSentAt(Map.of());

        var result = service.sendBulkEmail(new AdminBulkEmailRequest(TYPE, List.of(user.getId(), unknownId), false));

        assertEquals(1, result.sentCount());
        assertEquals(unknownId, result.failures().getFirst().userId());
        assertEquals(PROFILE_NOT_FOUND, result.failures().getFirst().reason());
    }

    @Test
    void sendBulkEmail_duplicateIds_areSentOnce() {
        UserEntity user = talent();
        givenUsers(user);
        givenLastSentAt(Map.of());

        var result = service.sendBulkEmail(new AdminBulkEmailRequest(TYPE, List.of(user.getId(), user.getId()), false));

        assertEquals(1, result.sentCount());
    }

    @Test
    void sendBulkEmail_onlyNullIds_throwsBadRequest() {
        List<UUID> ids = Arrays.asList((UUID) null);

        assertThrows(ApiException.class, () -> service.sendBulkEmail(new AdminBulkEmailRequest(TYPE, ids, false)));
    }
}
