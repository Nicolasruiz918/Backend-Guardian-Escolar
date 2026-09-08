package com.guardianescolar.api.shared.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class CorsPropertiesTest {

    @Test
    void storesAllowedOrigins() {
        CorsProperties properties = new CorsProperties(List.of("http://localhost:8081"));

        assertThat(properties.allowedOrigins()).containsExactly("http://localhost:8081");
    }
}
