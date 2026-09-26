package com.padimasso.autocasting.application.proposal.service.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProposalTokenGeneratorTest {

    private final ProposalTokenGenerator generator = new ProposalTokenGenerator();

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
