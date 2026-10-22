package com.padimasso.autocasting.application.email.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.email.service.EmailService;
import com.padimasso.autocasting.application.talent.model.BasicInfoEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TalentWelcomeEmailServiceImplTest {

    private static final String INCOMPLETE_TEMPLATE = "email/talent_onboarding_incomplete_email";

    @Mock
    private EmailService emailService;
    @Mock
    private SpringTemplateEngine templateEngine;
    @Mock
    private MessageSource messageSource;

    private TalentWelcomeEmailServiceImpl service;

    @BeforeEach
    void setUp() {
        AppProperties appProperties = new AppProperties();
        appProperties.setFrontendUrl("https://autocasting.app");
        appProperties.setBackendUrl("https://api.autocasting.app");
        service = new TalentWelcomeEmailServiceImpl(emailService, templateEngine, messageSource, appProperties);
    }

    private TalentProfileEntity incompleteProfile() {
        return TalentProfileEntity.builder()
            .id(UUID.randomUUID())
            .basicInfo(BasicInfoEntity.builder().stageName("María").build())
            .user(UserEntity.builder().email("maria@example.com").build())
            .build();
    }

    private void givenRendering() {
        when(messageSource.getMessage(eq("mail.talentIncomplete.subject"), isNull(), any())).thenReturn("Subject");
        when(templateEngine.process(eq(INCOMPLETE_TEMPLATE), any(IContext.class))).thenReturn("<html/>");
    }

    @Test
    void sendWelcomeEmail_withUnsubscribeToken_addsFooterLinkAndOneClickHeaders() {
        givenRendering();

        assertTrue(service.sendWelcomeEmail(incompleteProfile(), "abc"));

        ArgumentCaptor<Context> ctx = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq(INCOMPLETE_TEMPLATE), ctx.capture());
        assertEquals("https://autocasting.app/unsubscribe/abc", ctx.getValue().getVariable("unsubscribeUrl"));
        verify(emailService).sendHtmlEmail("maria@example.com", "Subject", "<html/>", Map.of(), Map.of(
            "List-Unsubscribe", "<https://api.autocasting.app/api/v1/email-preferences/abc/unsubscribe>",
            "List-Unsubscribe-Post", "List-Unsubscribe=One-Click"
        ));
    }

    @Test
    void sendWelcomeEmail_withoutUnsubscribeToken_hasNoLinkNorHeaders() {
        givenRendering();

        assertTrue(service.sendWelcomeEmail(incompleteProfile()));

        ArgumentCaptor<Context> ctx = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq(INCOMPLETE_TEMPLATE), ctx.capture());
        assertNull(ctx.getValue().getVariable("unsubscribeUrl"));
        verify(emailService).sendHtmlEmail("maria@example.com", "Subject", "<html/>", Map.of(), Map.of());
    }

    @Test
    void sendWelcomeEmail_missingStageName_skipsWithoutSending() {
        TalentProfileEntity profile = TalentProfileEntity.builder()
            .id(UUID.randomUUID())
            .user(UserEntity.builder().email("maria@example.com").build())
            .build();

        assertFalse(service.sendWelcomeEmail(profile, "abc"));
        verify(emailService, never()).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap(), anyMap());
    }
}
