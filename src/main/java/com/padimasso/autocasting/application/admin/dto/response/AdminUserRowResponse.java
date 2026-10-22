package com.padimasso.autocasting.application.admin.dto.response;

import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.auth.model.UserAccountProvider;
import com.padimasso.autocasting.application.auth.model.UserMode;
import com.padimasso.autocasting.application.email.model.EmailType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AdminUserRowResponse(
    UUID id,
    String email,
    String employerCompanyName,
    String talentStageName,
    List<String> roles,
    UserAccountProvider userAccountProvider,
    UserMode activeMode,
    OnboardingStatus talentOnboardingStatus,
    OnboardingStatus employerOnboardingStatus,
    String talentPublicSlug,
    LocalDateTime lastSavedAt,
    boolean emailingListSubscribed,
    List<EmailDelivery> emailDeliveries,
    boolean suspended,
    boolean deleted,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {
    public record EmailDelivery(
        EmailType type,
        LocalDateTime lastSentAt
    ) {
    }
}
