package com.padimasso.autocasting.application.admin.service;

import com.padimasso.autocasting.application.admin.dto.request.AdminBulkEmailRequest;
import com.padimasso.autocasting.application.admin.dto.response.AdminBulkEmailResultResponse;
import com.padimasso.autocasting.application.admin.dto.response.AdminEmailTypesResponse;

public interface AdminBulkEmailService {
    AdminEmailTypesResponse listEmailTypes();

    /**
     * Sends the chosen email to the selected users, skipping (with a reason) the ones that are inactive,
     * outside the email's audience, unsubscribed, or that got the same email within the resend cooldown
     * (the last one only unless {@code forceResend}). Sends are sequential and never abort the batch.
     */
    AdminBulkEmailResultResponse sendBulkEmail(AdminBulkEmailRequest request);
}
