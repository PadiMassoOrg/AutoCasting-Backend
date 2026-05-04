package com.padimasso.autocasting.config;

public class AppConstants {

    public static final String BASE_API_URL = "/api/v1";

    // Internal Usage:
    public static final int MAX_PAGE_SIZE = 20;

    // Test
    public static final String TEST_API_URL = BASE_API_URL + "/test";
    public static final String TEST_ACTOR_API_URL = TEST_API_URL + "/actor";
    public static final String TEST_CASTINERA_API_URL = TEST_API_URL + "/castinera";
    // Authentication
    public static final String REGISTER_API_URL = BASE_API_URL + "/auth/register";
    public static final String LOGIN_API_URL = BASE_API_URL + "/auth/login";
    public static final String ADMIN_LOGIN_API_URL = BASE_API_URL + "/admin/auth/login";
    public static final String ME_API_URL = BASE_API_URL + "/auth/me";
    public static final String ONBOARDING_API_URL = BASE_API_URL + "/auth/onboarding";
    public static final String FORGOT_PASS_URL = BASE_API_URL + "/auth/forgot-password";
    public static final String RESET_PASS_URL = BASE_API_URL + "/auth/reset-password";
    public static final String CHANGE_PASS_URL = BASE_API_URL + "/auth/change-password";
    // Site Metadata
    public static final String SITE_METADATA_URL = BASE_API_URL + "/sitemetadata";
    public static final String SITE_METADATA_VERSION_URL = SITE_METADATA_URL + "/version";
    // Database
    public static final String TALENT_DATABASE_API_URL = BASE_API_URL + "/talent-database";
    public static final String CASTING_DATABASE_API_URL = BASE_API_URL + "/castings-database";
    // Talent
    public static final String TALENT_PROFILE_API_URL = BASE_API_URL + "/talent";
    public static final String TALENT_CASTING_APPLICATIONS_URL = TALENT_PROFILE_API_URL + "/applications";
    public static final String CREDIT_API_URL = BASE_API_URL + "/credit";
    public static final String EDUCATION_API_URL = BASE_API_URL + "/education";
    public static final String TALENT_CASTING_APPLICATION_URL = BASE_API_URL + "/apply";
    // Employer
    public static final String EMPLOYER_PROFILE_API_URL = BASE_API_URL + "/employer";
    public static final String EMPLOYER_CASTINGS_URL = BASE_API_URL + "/employer/castings";
    public static final String EMPLOYER_CASTING_URL = BASE_API_URL + "/employer/casting";
    public static final String EMPLOYER_CASTING_APPLICATIONS_URL = BASE_API_URL + "/employer/applications";
    // Admin
    public static final String ADMIN_API_URL = BASE_API_URL + "/admin";
    public static final String ADMIN_USERS_API_URL = ADMIN_API_URL + "/users";
    public static final String ADMIN_NOTES_API_URL = ADMIN_API_URL + "/notes";
    public static final String ADMIN_NOTES_BY_ENTITY_API_URL = ADMIN_NOTES_API_URL + "/entities/{entityType}/{entityId}";
    public static final String ADMIN_NOTES_BY_RELATED_USER_API_URL = ADMIN_NOTES_API_URL + "/users/{userId}";
    public static final String ADMIN_NOTE_BY_ID_API_URL = ADMIN_NOTES_API_URL + "/{noteId}";
    public static final String ADMIN_USER_TALENT_PROFILE_API_URL = ADMIN_USERS_API_URL + "/{userId}/profiles/talent";
    public static final String ADMIN_USER_EMPLOYER_PROFILE_API_URL = ADMIN_USERS_API_URL + "/{userId}/profiles/employer";
    public static final String ADMIN_USER_BLOCK_API_URL = ADMIN_USERS_API_URL + "/{userId}/block";
    public static final String ADMIN_USER_RESTORE_API_URL = ADMIN_USERS_API_URL + "/{userId}/restore";
    // Casting - Public and Employer
    public static final String CASTING_DETAILS_URL = BASE_API_URL + "/casting";
    public static final String CASTING_BASIC_INFO_URL = CASTING_DETAILS_URL + "/basic-info";
    public static final String CASTING_ROLE_URL = CASTING_DETAILS_URL + "/role";
    public static final String CASTING_ROLE_ROLES_URL = CASTING_ROLE_URL + "/roles";
    public static final String CASTING_REQUIREMENT_URL = CASTING_DETAILS_URL + "/requirement";
    public static final String CASTING_REQUIREMENT_REQUIREMENTS_URL = CASTING_REQUIREMENT_URL + "/requirements";
    public static final String CASTING_REMUNERATION_URL = CASTING_DETAILS_URL + "/remuneration";
    public static final String CASTING_REMUNERATION_REMUNERATIONS_URL = CASTING_REMUNERATION_URL + "/remunerations";
    // Legal
    public static final String LEGAL_CURRENT_DOCUMENT_API_URL = BASE_API_URL + "/legal/current";
    public static final String LEGAL_REQUIREMENTS_API_URL = BASE_API_URL + "/legal/requirements";
    public static final String LEGAL_ACCEPT_DOCUMENT_API_URL = BASE_API_URL + "/legal/accept";
    public static final String LEGAL_ACCEPT_CURRENT_API_URL = BASE_API_URL + "/legal/accept-current";
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

    // Gender
    public static final String GENDER_OPTION_INDISTINCT = "sitemetadata.gender.indistinct";
    // Casting
    public static final String CASTING_STATUS_DRAFT = "sitemetadata.casting_status.draft";
    public static final String CASTING_STATUS_PUBLISHED = "sitemetadata.casting_status.published";
    public static final String CASTING_STATUS_CLOSED = "sitemetadata.casting_status.closed";
    public static final String CASTING_STATUS_PAUSED = "sitemetadata.casting_status.paused";
    public static final String CASTING_STATUS_ARCHIVED = "sitemetadata.casting_status.archived";
    public static final String CASTING_MODALITY_ON_SITE = "sitemetadata.casting_modality.on_site";
    // Section
    public static final String CASTING_SECTION_STATUS_NOT_STARTED = "sitemetadata.casting_section_status.not_started";
    public static final String CASTING_SECTION_STATUS_IN_PROGRESS = "sitemetadata.casting_section_status.in_progress";
    public static final String CASTING_SECTION_STATUS_COMPLETED = "sitemetadata.casting_section_status.completed";
    // Casting SiteMetadata
    public static final String CASTING_COMPENSATION_TYPE_PAID = "sitemetadata.compensation_type.paid";
    public static final String CASTING_COMPENSATION_TYPE_COLLABORATIVE = "sitemetadata.compensation_type.collaborative";
    public static final String PAY_RATE_TYPE_UNPAID = "sitemetadata.pay_rate_type.unpaid";
    public static final String PAY_RATE_TYPE_COLLABORATIVE = "sitemetadata.pay_rate_type.collaborative";
    public static final String CURRENCY_ARS = "sitemetadata.currency.ars";
    // Application
    public static final String CASTING_APPLICATION_STATUS_BLANK = "sitemetadata.application_status.blank";
    public static final String CASTING_APPLICATION_STATUS_VIEWED = "sitemetadata.application_status.viewed";
    public static final String CASTING_APPLICATION_STATUS_PRESELECTED = "sitemetadata.application_status.preselected";
    public static final String CASTING_APPLICATION_STATUS_SELECTED = "sitemetadata.application_status.selected";
    public static final String CASTING_APPLICATION_STATUS_NOT_PROCEEDING = "sitemetadata.application_status.not_proceeding";

    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }
}
