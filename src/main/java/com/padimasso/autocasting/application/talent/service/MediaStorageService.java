package com.padimasso.autocasting.application.talent.service;

public interface MediaStorageService {
    /**
     * Deletes the object backing a public Supabase Storage media URL (best-effort;
     * never throws). Does nothing if the URL is null/blank, doesn't belong to the
     * configured media bucket, or Supabase Storage config is missing.
     */
    void deleteByPublicUrl(String publicUrl);
}
