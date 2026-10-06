package com.padimasso.autocasting.application.employer.util;

import com.padimasso.autocasting.application.employer.dto.request.EmployerBasicInfoPatchRequest;
import com.padimasso.autocasting.exception.ApiException;

import java.util.UUID;
import java.util.regex.Pattern;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.GENERAL_INVALID_PARAMETER;

/**
 * Logos are never sent to the Backend as files: clients upload them straight to Supabase Storage and
 * send the resulting public URL. When an admin writes on an employer's behalf, a new logo URL must be
 * such an upload, stored in the configured media bucket under that employer's own
 * {@code employer/<profileId>/logo/<timestamp>.<ext>} path. Clearing the logo (null) is allowed; the
 * client deletes the stored file.
 */
public final class EmployerLogoUrlPolicy {

    private static final String PUBLIC_PATH = "/storage/v1/object/public/";
    private static final Pattern FILE_NAME = Pattern.compile("\\d+\\.[A-Za-z0-9]+");

    private EmployerLogoUrlPolicy() {
    }

    public static void requireManagedLogoUpload(
        EmployerBasicInfoPatchRequest request,
        String supabaseUrl,
        String mediaBucket,
        UUID profileId
    ) {
        if (!request.imageUrl().isPresent()) return;

        String url = request.imageUrl().orElse(null);
        if (url == null) return;

        if (!isManagedUpload(url, supabaseUrl, mediaBucket, profileId)) {
            throw ApiException.badRequest(GENERAL_INVALID_PARAMETER);
        }
    }

    static boolean isManagedUpload(String url, String supabaseUrl, String mediaBucket, UUID profileId) {
        if (isBlank(url) || isBlank(supabaseUrl) || isBlank(mediaBucket) || profileId == null) {
            return false;
        }

        String prefix = trimTrailingSlash(supabaseUrl) + PUBLIC_PATH + mediaBucket.trim() + "/employer/" + profileId + "/logo/";
        return url.startsWith(prefix) && FILE_NAME.matcher(url.substring(prefix.length())).matches();
    }

    private static String trimTrailingSlash(String value) {
        String v = value.trim();
        while (v.endsWith("/")) {
            v = v.substring(0, v.length() - 1);
        }
        return v;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
