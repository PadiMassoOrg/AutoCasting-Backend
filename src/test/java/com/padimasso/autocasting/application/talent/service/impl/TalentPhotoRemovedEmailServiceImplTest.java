package com.padimasso.autocasting.application.talent.service.impl;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.service.EmailService;
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
import org.springframework.core.io.Resource;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TalentPhotoRemovedEmailServiceImplTest {

    @Mock
    private EmailService emailService;
    @Mock
    private SpringTemplateEngine templateEngine;
    @Mock
    private MessageSource messageSource;

    private TalentPhotoRemovedEmailServiceImpl service;

    @BeforeEach
    void setUp() {
        AppProperties appProperties = new AppProperties();
        appProperties.setFrontendUrl("https://autocasting.app");
        appProperties.setBackendUrl("https://api.autocasting.app");
        service = new TalentPhotoRemovedEmailServiceImpl(emailService, templateEngine, messageSource, appProperties);
    }

    private TalentProfileEntity profile(String stageName, String email) {
        return TalentProfileEntity.builder()
            .id(UUID.randomUUID())
            .basicInfo(BasicInfoEntity.builder().stageName(stageName).build())
            .user(UserEntity.builder().email(email).build())
            .build();
    }

    @Test
    void sendPhotoRemovedEmail_rendersTemplateAndSends() {
        when(messageSource.getMessage(eq("mail.talentPhotoRemoved.subject"), isNull(), any())).thenReturn("Subject");
        when(templateEngine.process(eq(TalentPhotoRemovedEmailServiceImpl.TEMPLATE_NAME), any(IContext.class))).thenReturn("<html/>");

        boolean sent = service.sendPhotoRemovedEmail(profile("María", "maria@example.com"));

        assertTrue(sent);
        ArgumentCaptor<Map<String, Resource>> inline = ArgumentCaptor.captor();
        verify(emailService).sendHtmlEmail(eq("maria@example.com"), eq("Subject"), eq("<html/>"), inline.capture());
        assertEquals(List.of("talent-photo-example-1", "talent-photo-example-2"), List.copyOf(inline.getValue().keySet()));
        ArgumentCaptor<Context> ctx = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq(TalentPhotoRemovedEmailServiceImpl.TEMPLATE_NAME), ctx.capture());
        assertEquals("María", ctx.getValue().getVariable("talentName"));
        assertEquals("https://autocasting.app/dashboard/talent", ctx.getValue().getVariable("editProfileUrl"));
        assertEquals(List.of("talent-photo-example-1", "talent-photo-example-2"), ctx.getValue().getVariable("examplePhotoCids"));
    }

    @Test
    void sendPhotoRemovedEmail_missingEmail_skipsWithoutSending() {
        assertFalse(service.sendPhotoRemovedEmail(profile("María", null)));

        verify(emailService, never()).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void sendPhotoRemovedEmail_missingStageName_skipsWithoutSending() {
        assertFalse(service.sendPhotoRemovedEmail(profile(null, "maria@example.com")));

        verify(emailService, never()).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void sendPhotoRemovedEmail_sendFailure_returnsFalseWithoutThrowing() {
        when(messageSource.getMessage(eq("mail.talentPhotoRemoved.subject"), isNull(), any())).thenReturn("Subject");
        when(templateEngine.process(eq(TalentPhotoRemovedEmailServiceImpl.TEMPLATE_NAME), any(IContext.class))).thenReturn("<html/>");
        doThrow(new RuntimeException("smtp down")).when(emailService).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());

        assertFalse(service.sendPhotoRemovedEmail(profile("María", "maria@example.com")));
    }
}
