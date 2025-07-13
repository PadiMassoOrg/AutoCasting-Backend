package com.padimasso.autocasting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AutocastingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutocastingApplication.class, args);
    }

}
