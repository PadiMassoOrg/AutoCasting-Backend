package com.padimasso.autocasting.exception;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiExceptionClassifierTest {

    private final ApiExceptionClassifier classifier = new ApiExceptionClassifier();

    @Test
    void classifiesNotFoundKeysAs404() {
        ApiException exception = classifier.classify(new IllegalArgumentException("profile.not_found"));

        assertEquals(404, exception.getStatus().value());
        assertEquals("profile.not_found", exception.getMessageKey());
    }

    @Test
    void classifiesAuthenticationFailuresAs401() {
        ApiException exception = classifier.classify(new BadCredentialsException("Invalid credentials"));

        assertEquals(401, exception.getStatus().value());
        assertEquals("auth.invalid_credentials", exception.getMessageKey());
    }

    @Test
    void keepsPlainIllegalStateMessagesAs500() {
        ApiException exception = classifier.classify(new IllegalStateException("Cannot compute sitemetadata version"));

        assertEquals(500, exception.getStatus().value());
        assertEquals("Cannot compute sitemetadata version", exception.getMessageKey());
    }
}
