package com.padimasso.autocasting.application.admin.service.impl;

import com.padimasso.autocasting.application.admin.dto.request.AdminBulkEmailRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminBulkEmailResultResponse.Failure;
import com.padimasso.autocasting.application.admin.dto.response.AdminBulkEmailResultResponse.SkipReason;
import com.padimasso.autocasting.application.admin.dto.response.AdminBulkEmailResultResponse.Skipped;
import com.padimasso.autocasting.application.admin.dto.response.AdminBulkEmailResultResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminEmailTypesResponse;
import com.padimasso.autocasting.application.admin.service.AdminBulkEmailService;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.email.model.EmailType;
import com.padimasso.autocasting.application.email.service.EmailDeliveryService;
import com.padimasso.autocasting.application.email.service.EmailPreferencesService;
import com.padimasso.autocasting.application.email.service.sender.BulkEmailRecipient;
import com.padimasso.autocasting.application.email.service.sender.BulkEmailSenderRegistry;
import com.padimasso.autocasting.application.email.util.EmailAudienceEligibility;
import com.padimasso.autocasting.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.padimasso.autocasting.config.AppConstants.EMAIL_RESEND_COOLDOWN_DAYS;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.GENERAL_IDS_REQUIRED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROFILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AdminBulkEmailServiceImpl implements AdminBulkEmailService {

    private final UserRepository userRepository;
    private final EmailDeliveryService emailDeliveryService;
    private final EmailPreferencesService emailPreferencesService;
    private final BulkEmailSenderRegistry bulkEmailSenderRegistry;

    @Override
    public AdminEmailTypesResponse listEmailTypes() {
        var types = Arrays.stream(EmailType.values())
            .map(type -> new AdminEmailTypesResponse.Item(type, type.getAudience()))
            .toList();
        return new AdminEmailTypesResponse(types, EMAIL_RESEND_COOLDOWN_DAYS);
    }

    @Override
    public AdminBulkEmailResultResponse sendBulkEmail(AdminBulkEmailRequest request) {
        List<UUID> uniqueUserIds = request.userIds().stream()
            .filter(Objects::nonNull)
            .distinct()
            .toList();

        if (uniqueUserIds.isEmpty()) {
            throw ApiException.badRequest(GENERAL_IDS_REQUIRED);
        }

        EmailType emailType = request.emailType();
        Map<UUID, UserEntity> usersById = userRepository
            .findAllIncludingDeleted((root, query, cb) -> root.get("id").in(uniqueUserIds)).stream()
            .collect(Collectors.toMap(UserEntity::getId, Function.identity()));
        Map<UUID, LocalDateTime> lastSentAt = emailDeliveryService.findLastSentAt(uniqueUserIds, emailType);
        LocalDateTime cooldownStart = LocalDateTime.now().minusDays(EMAIL_RESEND_COOLDOWN_DAYS);

        List<Skipped> skipped = new ArrayList<>();
        List<Failure> failures = new ArrayList<>();
        List<BulkEmailRecipient> recipients = new ArrayList<>();

        for (UUID userId : uniqueUserIds) {
            UserEntity user = usersById.get(userId);
            if (user == null) {
                failures.add(new Failure(userId, PROFILE_NOT_FOUND));
                continue;
            }

            SkipReason skipReason = skipReason(user, emailType, lastSentAt.get(userId), cooldownStart, request.forceResend());
            if (skipReason != null) {
                skipped.add(new Skipped(userId, skipReason));
                continue;
            }

            recipients.add(new BulkEmailRecipient(user, emailPreferencesService.ensureUnsubscribeToken(user)));
        }

        Map<UUID, String> sendFailures = recipients.isEmpty()
            ? Map.of()
            : bulkEmailSenderRegistry.forType(emailType).send(recipients);
        sendFailures.forEach((userId, reason) -> failures.add(new Failure(userId, reason)));

        List<UUID> sentUserIds = recipients.stream()
            .map(recipient -> recipient.user().getId())
            .filter(userId -> !sendFailures.containsKey(userId))
            .toList();
        emailDeliveryService.recordAll(sentUserIds, emailType);

        return new AdminBulkEmailResultResponse(sentUserIds.size(), skipped.size(), failures.size(), skipped, failures);
    }

    private static SkipReason skipReason(
        UserEntity user,
        EmailType emailType,
        LocalDateTime lastSentAt,
        LocalDateTime cooldownStart,
        boolean forceResend
    ) {
        if (user.isDeleted() || user.isSuspended()) return SkipReason.USER_INACTIVE;
        if (!EmailAudienceEligibility.isEligible(emailType.getAudience(), user)) return SkipReason.AUDIENCE_MISMATCH;
        if (user.getMarketingEmailsUnsubscribedAt() != null) return SkipReason.UNSUBSCRIBED;
        if (!forceResend && lastSentAt != null && lastSentAt.isAfter(cooldownStart)) return SkipReason.RECENTLY_SENT;
        return null;
    }
}
