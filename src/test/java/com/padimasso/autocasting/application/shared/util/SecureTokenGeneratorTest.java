package com.padimasso.autocasting.application.shared.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecureTokenGeneratorTest {

    private final SecureTokenGenerator generator = new SecureTokenGenerator();

    @Test
    void generate_returns43CharBase64UrlWithoutPadding() {
        String token = generator.generate();

        assertEquals(43, token.length());
        assertTrue(token.matches("[A-Za-z0-9_-]+"));
    }

    @Test
    void generate_returnsDifferentTokensOnEachCall() {
        assertNotEquals(generator.generate(), generator.generate());
    }
}
