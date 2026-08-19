package com.padimasso.autocasting.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
    private String frontendUrl;
    private String backendUrl;
    private String oauthSuccessUrl;
    private SupabaseProperties supabase = new SupabaseProperties();
    private GoogleProperties google = new GoogleProperties();

    @Getter
    @Setter
    public static class SupabaseProperties {
        private String url;
        private String serviceRoleKey;
        private String legalBucket = "legal-documents-private";
        private String legalPrefix = "legal/documents";
        private int legalPdfSignedUrlTtlSeconds = 900;
    }

    @Getter
    @Setter
    public static class GoogleProperties {
        private String mobileIosClientId;
        private String mobileAndroidClientId;
    }
}
