package com.padimasso.autocasting.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpStatus;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiErrorFactoryTest {

    @Test
    void resolvesKnownMessageKeysAsServerCodes() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("auth.invalid_credentials", Locale.ROOT, "auth.invalid_credentials");
        ApiErrorFactory factory = new ApiErrorFactory(messageSource, new ObjectMapper());

        ApiErrorResponse response = factory.build(
            HttpStatus.UNAUTHORIZED,
            "auth.invalid_credentials",
            null,
            "/api/auth/login",
            Locale.ENGLISH
        );

        assertEquals(401, response.status());
        assertEquals("auth.invalid_credentials", response.message());
        assertTrue(response.messageArgs().isEmpty());
        assertEquals("/api/auth/login", response.path());
    }

    @Test
    void fallsBackToRawMessageWhenTranslationDoesNotExist() {
        ApiErrorFactory factory = new ApiErrorFactory(new StaticMessageSource(), new ObjectMapper());

        ApiErrorResponse response = factory.build(
            HttpStatus.BAD_REQUEST,
            "castings.section.not_found",
            null,
            "/api/castings/section",
            Locale.ENGLISH
        );

        assertEquals("castings.section.not_found", response.message());
        assertTrue(response.messageArgs().isEmpty());
    }

    @Test
    void serializesMessageArgumentsAsStrings() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("general.missing_parameter", Locale.ROOT, "server_error.general.missing_parameter");
        ApiErrorFactory factory = new ApiErrorFactory(messageSource, new ObjectMapper());

        ApiErrorResponse response = factory.build(
            HttpStatus.BAD_REQUEST,
            "general.missing_parameter",
            new Object[]{"locale"},
            "/api/legal/current",
            Locale.ENGLISH
        );

        assertEquals("server_error.general.missing_parameter", response.message());
        assertEquals(java.util.List.of("locale"), response.messageArgs());
    }

    @Test
    void buildsStructuredFieldErrors() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("auth.required_field", Locale.ROOT, "server_error.auth.required_field");
        ApiErrorFactory factory = new ApiErrorFactory(messageSource, new ObjectMapper());

        ApiErrorMessage fieldError = factory.buildMessage("auth.required_field", null);

        assertEquals("server_error.auth.required_field", fieldError.message());
        assertTrue(fieldError.messageArgs().isEmpty());
    }
}
