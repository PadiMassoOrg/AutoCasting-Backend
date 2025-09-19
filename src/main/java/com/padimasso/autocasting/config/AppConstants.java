package com.padimasso.autocasting.config;

public class AppConstants {
    public static final String BASE_API_URL = "/api/v1";

    // Authentication
    public static final String LOGIN_API_URL = BASE_API_URL + "/auth/login";
    public static final String REGISTER_API_URL = BASE_API_URL + "/auth/register";
    public static final String FORGOT_PASS_URL = BASE_API_URL + "/auth/forgot-password";
    public static final String RESET_PASS_URL = BASE_API_URL + "/auth/reset-password";

    // Test
    public static final String TEST_API_URL = BASE_API_URL + "/test";
    public static final String TEST_ACTOR_API_URL = TEST_API_URL + "/actor";
    public static final String TEST_CASTINERA_API_URL = TEST_API_URL + "/castinera";

    // Site Metadata
    public static final String SITE_METADATA_URL = BASE_API_URL + "/sitemetadata";
    public static final String SITE_METADATA_VERSION_URL = SITE_METADATA_URL + "/version";

    // Profile
    public static final String PROFILE_API_URL = BASE_API_URL + "/profile";
    public static final String TALENT_DATABASE_API_URL = BASE_API_URL + "/profile/talent-database";

    // Credit
    public static final String CREDIT_API_URL = BASE_API_URL + "/credit";

    // Education
    public static final String EDUCATION_API_URL = BASE_API_URL + "/education";

    // Security
    public static final String ISSUER = "Auto-Casting";
    public static final String SECRET = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";
    public static final long EXPIRATION_TIME = 86400; // 10 days
    public static final long RESET_PASSWORD_EXPIRATION_TIME = 900; // 15min
    public static final long RESET_PASSWORD_EXPIRATION_TIME_MIN = 15; // 15min

    // Company
    public static final String SUPPORT_EMAIL = "support@autocasting.com";
    public static final String INSTA_URL = "instagram.com";
    public static final String LINKEDIN_URL = "linkedin.com";

    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }
}
