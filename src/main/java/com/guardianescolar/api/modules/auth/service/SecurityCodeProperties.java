package com.guardianescolar.api.modules.auth.service;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "guardian.security.codes")
public record SecurityCodeProperties(
        Duration expiration,
        int maxAttempts) {
}
