package com.padimasso.autocasting.config.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.padimasso.autocasting",
    repositoryBaseClass = SoftDeleteRepositoryImpl.class
)
@SuppressWarnings("unused")
public class JpaConfig {}
