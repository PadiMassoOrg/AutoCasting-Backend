package com.padimasso.autocasting.application.email.service.sender;

import com.padimasso.autocasting.application.email.model.EmailType;
import com.padimasso.autocasting.application.email.service.TalentWelcomeEmailService;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.GENERAL_UNEXPECTED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROFILE_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class TalentWelcomeBulkEmailSender implements BulkEmailSender {

    private final TalentProfileRepository talentProfileRepository;
    private final TalentWelcomeEmailService talentWelcomeEmailService;

    @Override
    public EmailType type() {
        return EmailType.TALENT_WELCOME;
    }

    @Override
    public Map<UUID, String> send(List<BulkEmailRecipient> recipients) {
        List<UUID> userIds = recipients.stream().map(recipient -> recipient.user().getId()).toList();
        Map<UUID, TalentProfileEntity> profilesByUserId = new HashMap<>();
        if (!userIds.isEmpty()) {
            talentProfileRepository.findAllByUserIdInForAdmin(userIds)
                .forEach(profile -> profilesByUserId.put(profile.getUser().getId(), profile));
        }

        Map<UUID, String> failures = new LinkedHashMap<>();
        for (BulkEmailRecipient recipient : recipients) {
            UUID userId = recipient.user().getId();
            TalentProfileEntity profile = profilesByUserId.get(userId);
            if (profile == null) {
                failures.put(userId, PROFILE_NOT_FOUND);
            } else if (!talentWelcomeEmailService.sendWelcomeEmail(profile, recipient.unsubscribeToken())) {
                failures.put(userId, GENERAL_UNEXPECTED);
            }
        }
        return failures;
    }
}
