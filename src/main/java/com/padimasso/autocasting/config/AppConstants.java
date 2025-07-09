package com.padimasso.autocasting.config;

public class AppConstants {

    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }

    /*
     * API Constants
     */
    public static final String BASE_API_URL = "/api/v1";
    public static final String BY_ID_PARAM = "/{id}";
    public static final String IMAGE_BY_PROFILE_ID = "/{id}/image";


    // Authentication
    public static final String LOGIN_API_URL = BASE_API_URL + "/auth/login";
    public static final String REGISTER_API_URL = BASE_API_URL + "/auth/register";

    // Test
    public static final String TEST_API_URL = BASE_API_URL + "/test";
    public static final String TEST_USER_API_URL = TEST_API_URL + "/user";
    public static final String TEST_ADMIN_API_URL = TEST_API_URL + "/admin";

    /*
     * Spring Security
     */
    public static final String ISSUER = "Auto-Casting";
    public static final String SECRET = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";
    public static final long EXPIRATION_TIME = 86400; // 10 days

    /*
     * Spring Security
     */
}