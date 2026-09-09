package com.guardianescolar.api.shared.security;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "guardian.security.jwt")
public record JwtProperties(
        String secret,
        Duration accessExpiration,
        Duration refreshExpiration) {
}
