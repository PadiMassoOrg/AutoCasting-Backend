package com.padimasso.autocasting.application.shared.util;

public final class TextNormalizer {

    private TextNormalizer() {
    }

    public static String normalizeNullable(String value) {
        if (value == null) return null;
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
