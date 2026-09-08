package com.guardianescolar.api.shared.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "guardian.cors")
public record CorsProperties(List<String> allowedOrigins) {
}
