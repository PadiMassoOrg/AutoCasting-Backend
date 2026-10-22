package com.padimasso.autocasting.application.email.service;

import com.padimasso.autocasting.application.auth.model.UserEntity;

public interface EmailPreferencesService {
    /**
     * Opts the token's user out of marketing emails. Idempotent: an already unsubscribed user stays
     * unsubscribed with the original date. Throws not found when the token matches no user.
     */
    void unsubscribe(String token);

    /**
     * Returns the user's unsubscribe token, creating and storing one on the first marketing email.
     */
    String ensureUnsubscribeToken(UserEntity user);
}
