package com.padimasso.autocasting.application.profile.repository;

import com.padimasso.autocasting.application.profile.model.SocialMediaEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface SocialMediaRepository extends SoftDeleteRepository<SocialMediaEntity, UUID> {
}
