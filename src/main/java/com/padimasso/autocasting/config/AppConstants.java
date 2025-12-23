package com.padimasso.autocasting.config;

public class AppConstants {
    public static final String BASE_API_URL = "/api/v1";

    // Authentication
    public static final String REGISTER_API_URL = BASE_API_URL + "/auth/register";
    public static final String LOGIN_API_URL = BASE_API_URL + "/auth/login";
    public static final String ME_API_URL = BASE_API_URL + "/auth/me";
    public static final String ONBOARDING_API_URL = BASE_API_URL + "/auth/onboarding";
    public static final String FORGOT_PASS_URL = BASE_API_URL + "/auth/forgot-password";
    public static final String RESET_PASS_URL = BASE_API_URL + "/auth/reset-password";
    public static final String CHANGE_PASS_URL = BASE_API_URL + "/auth/change-password";

    // Test
    public static final String TEST_API_URL = BASE_API_URL + "/test";
    public static final String TEST_ACTOR_API_URL = TEST_API_URL + "/actor";
    public static final String TEST_CASTINERA_API_URL = TEST_API_URL + "/castinera";

    // Site Metadata
    public static final String SITE_METADATA_URL = BASE_API_URL + "/sitemetadata";
    public static final String SITE_METADATA_VERSION_URL = SITE_METADATA_URL + "/version";

    // Database
    public static final String TALENT_DATABASE_API_URL = BASE_API_URL + "/talent-database";
    public static final String CASTING_DATABASE_API_URL = BASE_API_URL + "/castings-database";

    // Talent
    public static final String TALENT_PROFILE_API_URL = BASE_API_URL + "/talent";
    public static final String CREDIT_API_URL = BASE_API_URL + "/credit";
    public static final String EDUCATION_API_URL = BASE_API_URL + "/education";

    // Employer
    public static final String EMPLOYER_PROFILE_API_URL = BASE_API_URL + "/employer";
    public static final String EMPLOYER_CASTINGS_URL = BASE_API_URL + "/employer/castings";

    // Legal
    public static final String LEGAL_CURRENT_DOCUMENT_API_URL = BASE_API_URL + "/legal/current";
    public static final String LEGAL_ACCEPT_DOCUMENT_API_URL = BASE_API_URL + "/legal/accept";

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

    // SiteMetadata & Internal Usage:
    public static final int MAX_PAGE_SIZE = 20;
    public static final String CASTING_STATUS_DRAFT = "sitemetadata.casting_status.draft";
    public static final String CASTING_STATUS_PUBLISHED = "sitemetadata.casting_status.published";
    public static final String SECTION_STATUS_NOT_STARTED = "sitemetadata.casting_section_status.not_started";
    public static final String COMPENSATION_TYPE_UNPAID = "sitemetadata.compensation_type.unpaid";

    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }
}
