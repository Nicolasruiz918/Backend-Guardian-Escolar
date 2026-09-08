package com.guardianescolar.api.shared.health;

import java.time.Instant;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private static final String DEFAULT_VERSION = "0.0.1-SNAPSHOT";

    private final String version;

    public HealthController(ObjectProvider<BuildProperties> buildProperties) {
        this(buildProperties.getIfAvailable());
    }

    HealthController(BuildProperties buildProperties) {
        this.version = buildProperties == null ? DEFAULT_VERSION : buildProperties.getVersion();
    }

    @GetMapping("/health")
    ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse(
                "UP",
                "guardian-escolar-api",
                version,
                Instant.now()));
    }
}
