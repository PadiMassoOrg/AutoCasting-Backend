package com.padimasso.autocasting.application.sitemetadata.repository;

import com.padimasso.autocasting.application.sitemetadata.model.SocialMediaOptionEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;

import java.util.UUID;

public interface SocialMediaOptionRepository  extends SoftDeleteRepository<SocialMediaOptionEntity, UUID> {
}
