package com.padimasso.autocasting.exception;

public final class ErrorMessageKeys {

    public static final String AUTH_ACCESS_DENIED = "auth.access_denied";
    public static final String AUTH_CURRENT_PASSWORD_MISMATCH = "auth.current_password_mismatch";
    public static final String AUTH_INVALID_CREDENTIALS = "auth.invalid_credentials";
    public static final String AUTH_INVALID_PLAN = "auth.invalid_plan";
    public static final String AUTH_INVALID_PRINCIPAL = "auth.invalid_principal";
    public static final String AUTH_INVALID_ROLE = "auth.invalid_role";
    public static final String AUTH_INVALID_TOKEN = "auth.invalid_token";
    public static final String AUTH_NOT_AUTHENTICATED = "auth.not_authenticated";
    public static final String AUTH_PASSWORD_RESET_EXTERNAL = "auth.password_reset_external";
    public static final String AUTH_TOKEN_EXPIRED = "auth.token_expired";
    public static final String AUTH_USER_EXISTS = "auth.user_exists";
    public static final String AUTH_USER_NOT_FOUND = "auth.user_not_found";

    public static final String PROFILE_NOT_FOUND = "profile.not_found";

    public static final String GENERAL_DATA_INTEGRITY_VIOLATION = "general.data_integrity_violation";
    public static final String GENERAL_ENDPOINT_NOT_FOUND = "general.endpoint_not_found";
    public static final String GENERAL_INVALID_PARAMETER = "general.invalid_parameter";
    public static final String GENERAL_INVALID_REQUEST_BODY = "general.invalid_request_body";
    public static final String GENERAL_METHOD_NOT_ALLOWED = "general.method_not_allowed";
    public static final String GENERAL_MISSING_PARAMETER = "general.missing_parameter";
    public static final String GENERAL_REQUIRED_ONE = "general.required_one";
    public static final String GENERAL_RESOURCE_NOT_FOUND = "general.resource_not_found";
    public static final String GENERAL_SLUG_REQUIRED = "general.slug_required";
    public static final String GENERAL_ROLE_ID_REQUIRED = "general.role_id_required";
    public static final String GENERAL_UNEXPECTED = "general.unexpected";

    public static final String APPLICATIONS_ALREADY_APPLIED = "applications.already_applied";
    public static final String APPLICATIONS_NOT_FOUND_OR_FORBIDDEN = "applications.not_found_or_forbidden";

    public static final String CASTING_ID_REQUIRED = "casting.id_required";
    public static final String CASTING_ROLE_REQUIRED = "casting.role.required";
    public static final String CASTING_ROLE_NOT_FOUND = "casting.role.not_found";
    public static final String CASTING_ROLE_REMUNERATION_NOT_FOUND = "casting.role.remuneration.not_found";
    public static final String CASTING_ROLE_REQUIREMENT_ALREADY_EXISTS = "casting.role.requirement.already_exists";

    public static final String CASTINGS_NOT_FOUND = "castings.not_found";
    public static final String CASTINGS_NOT_PUBLISHABLE = "castings.not_publishable";
    public static final String CASTINGS_DEADLINE_REQUIRED = "castings.deadline_required";
    public static final String CASTINGS_DEADLINE_PASSED = "castings.deadline_passed";
    public static final String CASTINGS_INVALID_STATUS_TRANSITION = "castings.invalid_status_transition";
    public static final String CASTINGS_SECTION_NOT_FOUND = "castings.section.not_found";
    public static final String CASTINGS_SECTION_MISMATCH = "castings.section.mismatch";
    public static final String CASTINGS_ROLE_MISMATCH = "castings.role.mismatch";

    private ErrorMessageKeys() {
        throw new IllegalStateException("Utility class");
    }
}
