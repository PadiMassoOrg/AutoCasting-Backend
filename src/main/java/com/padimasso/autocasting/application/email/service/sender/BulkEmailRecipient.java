package com.padimasso.autocasting.application.email.service.sender;

import com.padimasso.autocasting.application.auth.model.UserEntity;

public record BulkEmailRecipient(UserEntity user, String unsubscribeToken) {
}
