package com.padimasso.autocasting.application.email.util;

import com.padimasso.autocasting.config.AppConstants;
import com.padimasso.autocasting.config.AppProperties;

import java.util.Map;

/**
 * Builds the unsubscribe links of a marketing email: the page linked from the email footer (Frontend,
 * which unsubscribes and redirects home) and the RFC 8058 one-click headers mail clients use directly.
 */
public final class EmailUnsubscribeLinks {

    static final String FRONTEND_UNSUBSCRIBE_PATH = "/unsubscribe/";

    private EmailUnsubscribeLinks() {
        throw new IllegalStateException("Utility class");
    }

    public static String pageUrl(AppProperties appProperties, String token) {
        return appProperties.getFrontendUrl() + FRONTEND_UNSUBSCRIBE_PATH + token;
    }

    public static Map<String, String> headers(AppProperties appProperties, String token) {
        String oneClickUrl = appProperties.getBackendUrl()
            + AppConstants.EMAIL_UNSUBSCRIBE_API_URL.replace("{token}", token);
        return Map.of(
            "List-Unsubscribe", "<" + oneClickUrl + ">",
            "List-Unsubscribe-Post", "List-Unsubscribe=One-Click"
        );
    }
}
