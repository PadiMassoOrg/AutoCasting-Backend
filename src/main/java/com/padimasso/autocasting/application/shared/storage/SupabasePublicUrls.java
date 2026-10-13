package com.padimasso.autocasting.application.shared.storage;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class SupabasePublicUrls {

    private SupabasePublicUrls() {
    }

    // Object key of a public URL inside the given bucket, or null when the URL belongs to another bucket or host.
    public static String objectKey(String publicUrl, String bucket) {
        if (publicUrl == null || publicUrl.isBlank() || bucket == null || bucket.isBlank()) return null;

        String clean = publicUrl.split("#")[0].split("\\?")[0];
        String marker = "/storage/v1/object/public/" + bucket + "/";
        int idx = clean.indexOf(marker);
        if (idx == -1) return null;

        String key = URLDecoder.decode(clean.substring(idx + marker.length()), StandardCharsets.UTF_8);
        return key.isBlank() ? null : key;
    }
}
