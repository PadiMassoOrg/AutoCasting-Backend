package com.padimasso.autocasting.application.shared.storage;

import java.time.Instant;

// An object listed from Supabase Storage; createdAt is null when the listing did not report it.
public record StoredObject(String key, Instant createdAt) {
}
