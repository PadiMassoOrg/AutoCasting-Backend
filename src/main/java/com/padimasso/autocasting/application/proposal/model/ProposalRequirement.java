package com.padimasso.autocasting.application.proposal.model;

import com.padimasso.autocasting.application.auth.model.OnboardingStatus;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.model.UserMode;

public enum ProposalRequirement {
    EMPLOYER_ONBOARDING_COMPLETED(UserMode.EMPLOYER) {
        @Override
        public boolean isSatisfiedBy(UserEntity user) {
            return user.getEmployerOnboardingStatus() == OnboardingStatus.COMPLETED;
        }
    };

    private final UserMode requiredMode;

    ProposalRequirement(UserMode requiredMode) {
        this.requiredMode = requiredMode;
    }

    public UserMode requiredMode() {
        return requiredMode;
    }

    public abstract boolean isSatisfiedBy(UserEntity user);
}
