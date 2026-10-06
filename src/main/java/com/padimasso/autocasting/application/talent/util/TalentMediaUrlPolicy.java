package com.padimasso.autocasting.application.talent.util;

import com.padimasso.autocasting.application.talent.dto.request.MediaPatchRequest;
import com.padimasso.autocasting.application.talent.dto.request.OtherPicturePatch;
import com.padimasso.autocasting.exception.ApiException;

import java.util.UUID;
import java.util.regex.Pattern;

import static com.padimasso.autocasting.exception.ErrorMessageKeys.GENERAL_INVALID_PARAMETER;

/**
 * Photos are never sent to the Backend as files: clients upload them straight to Supabase Storage and
 * send the resulting public URL. When an admin writes on a talent's behalf, every photo URL must be
 * such an upload, stored in the configured media bucket under that talent's own
 * {@code talent/<profileId>/media/<slot>/<timestamp>.<ext>} folder. Clearing a photo goes through the
 * dedicated removal endpoint (which also deletes the stored file), never through this patch.
 */
public final class TalentMediaUrlPolicy {

    private static final String PUBLIC_PATH = "/storage/v1/object/public/";
    private static final Pattern FILE_NAME = Pattern.compile("\\d+\\.[A-Za-z0-9]+");

    private TalentMediaUrlPolicy() {
    }

    public static void requireManagedPhotoUploads(
        MediaPatchRequest request,
        String supabaseUrl,
        String mediaBucket,
        UUID profileId
    ) {
        if (request.headshotImageUrl().isPresent()) {
            requireUpload(request.headshotImageUrl().orElse(null), supabaseUrl, mediaBucket, profileId, "headshot");
        }
        if (request.fullBodyImageUrl().isPresent()) {
            requireUpload(request.fullBodyImageUrl().orElse(null), supabaseUrl, mediaBucket, profileId, "fullbody");
        }
        if (request.otherPictures() != null) {
            for (OtherPicturePatch picture : request.otherPictures()) {
                requireUpload(picture.url(), supabaseUrl, mediaBucket, profileId, "other");
            }
        }
    }

    static void requireUpload(String url, String supabaseUrl, String mediaBucket, UUID profileId, String slotFolder) {
        if (!isManagedUpload(url, supabaseUrl, mediaBucket, profileId, slotFolder)) {
            throw ApiException.badRequest(GENERAL_INVALID_PARAMETER);
        }
    }

    static boolean isManagedUpload(String url, String supabaseUrl, String mediaBucket, UUID profileId, String slotFolder) {
        if (isBlank(url) || isBlank(supabaseUrl) || isBlank(mediaBucket) || profileId == null) {
            return false;
        }

        String prefix = trimTrailingSlash(supabaseUrl) + PUBLIC_PATH + mediaBucket.trim() + "/talent/" + profileId + "/media/"
            + slotFolder + "/";
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
