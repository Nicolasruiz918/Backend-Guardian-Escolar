package com.guardianescolar.api.modules.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardianescolar.api.modules.auth.service.SecurityCodeGenerator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void profileAndPreferencesRequireOwnerSession() throws Exception {
        String token = registerAndToken();

        mockMvc.perform(get("/profile").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acudiente Demo"));

        mockMvc.perform(patch("/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Acudiente Actualizado","phone":"+573001112233"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acudiente Actualizado"))
                .andExpect(jsonPath("$.phone").value("+573001112233"));

        mockMvc.perform(patch("/profile/preferences")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"language":"es","timezone":"America/Bogota","pushEnabled":true,"emailEnabled":false,"smsEnabled":true,
                                "locationAlertsEnabled":true,"routeAlertsEnabled":false,"safeZoneAlertsEnabled":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.smsEnabled").value(true))
                .andExpect(jsonPath("$.routeAlertsEnabled").value(false));
    }

    private String registerAndToken() throws Exception {
        String email = "profile-" + UUID.randomUUID() + "@example.com";
        String response = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Acudiente Demo","phone":"+573001234567","email":"%s","password":"Demo@1234"}
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode json = objectMapper.readTree(response);
        return json.get("token").asText();
    }

    @TestConfiguration
    static class TestCodeConfiguration {

        @Bean
        @Primary
        SecurityCodeGenerator securityCodeGenerator() {
            AtomicInteger sequence = new AtomicInteger();
            return new SecurityCodeGenerator() {
                @Override
                public String sixDigitCode() {
                    return "123456";
                }

                @Override
                public String opaqueToken() {
                    return "profile-token-" + sequence.incrementAndGet() + "-" + UUID.randomUUID();
                }
            };
        }
    }
}
