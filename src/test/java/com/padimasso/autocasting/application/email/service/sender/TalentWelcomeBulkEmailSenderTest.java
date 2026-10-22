package com.padimasso.autocasting.application.email.service.sender;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.email.service.TalentWelcomeEmailService;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.GENERAL_UNEXPECTED;
import static com.padimasso.autocasting.exception.ErrorMessageKeys.PROFILE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TalentWelcomeBulkEmailSenderTest {

    @Mock
    private TalentProfileRepository talentProfileRepository;
    @Mock
    private TalentWelcomeEmailService talentWelcomeEmailService;

    private TalentWelcomeBulkEmailSender sender;

    @BeforeEach
    void setUp() {
        sender = new TalentWelcomeBulkEmailSender(talentProfileRepository, talentWelcomeEmailService);
    }

    private TalentProfileEntity profileOf(UserEntity user) {
        return TalentProfileEntity.builder().id(UUID.randomUUID()).user(user).build();
    }

    @Test
    void send_passesEachRecipientsUnsubscribeTokenAndReportsFailures() {
        UserEntity sent = UserEntity.builder().id(UUID.randomUUID()).build();
        UserEntity failed = UserEntity.builder().id(UUID.randomUUID()).build();
        UserEntity withoutProfile = UserEntity.builder().id(UUID.randomUUID()).build();
        TalentProfileEntity sentProfile = profileOf(sent);
        TalentProfileEntity failedProfile = profileOf(failed);
        when(talentProfileRepository.findAllByUserIdInForAdmin(anyList())).thenReturn(List.of(sentProfile, failedProfile));
        when(talentWelcomeEmailService.sendWelcomeEmail(sentProfile, "t1")).thenReturn(true);
        when(talentWelcomeEmailService.sendWelcomeEmail(failedProfile, "t2")).thenReturn(false);

        Map<UUID, String> failures = sender.send(List.of(
            new BulkEmailRecipient(sent, "t1"),
            new BulkEmailRecipient(failed, "t2"),
            new BulkEmailRecipient(withoutProfile, "t3")
        ));

        assertEquals(Map.of(failed.getId(), GENERAL_UNEXPECTED, withoutProfile.getId(), PROFILE_NOT_FOUND), failures);
    }
}
