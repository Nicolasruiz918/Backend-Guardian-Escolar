package com.guardianescolar.api.modules.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardianescolar.api.modules.auth.repository.UserAccountRepository;
import com.guardianescolar.api.modules.auth.service.SecurityCodeGenerator;
import java.util.Map;
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
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserAccountRepository userRepository;

    @Test
    void registerCreatesUserWithHashedPasswordAndSession() throws Exception {
        String email = uniqueEmail();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "Padre Demo",
                                "email", email,
                                "password", "Demo@1234",
                                "phone", "+57 300 123 4567"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(email));

        assertThat(userRepository.findByEmailIgnoreCase(email))
                .hasValueSatisfying(user -> assertThat(user.getPasswordHash()).doesNotContain("Demo@1234"));
    }

    @Test
    void registerRejectsDuplicateEmail() throws Exception {
        String email = uniqueEmail();
        register(email, "Demo@1234");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "Duplicado",
                                "email", email,
                                "password", "Demo@1234",
                                "phone", "+57 300 000 0000"))))
                .andExpect(status().isConflict());
    }

    @Test
    void registerRejectsWeakPassword() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "Padre Demo",
                                "email", uniqueEmail(),
                                "password", "password",
                                "phone", "+57 300 123 4567"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginAcceptsValidCredentialsAndRejectsWrongCredentials() throws Exception {
        String email = uniqueEmail();
        register(email, "Demo@1234");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", "Demo@1234"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.requires2FA").value(false));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", "Wrong@1234"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointRequiresValidJwt() throws Exception {
        mockMvc.perform(patch("/auth/account/2fa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("enabled", true, "method", "email"))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(patch("/auth/account/2fa")
                        .header("Authorization", "Bearer invalid.token.value")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("enabled", true, "method", "email"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshTokenCanBeRotatedAndRevokedOnLogout() throws Exception {
        AuthPayload session = register(uniqueEmail(), "Demo@1234");

        MvcResult refreshed = mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("refreshToken", session.refreshToken()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();
        AuthPayload rotated = readAuth(refreshed);

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + rotated.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("refreshToken", rotated.refreshToken()))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("refreshToken", rotated.refreshToken()))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void passwordResetRequiresValidCodeAndAllowsNewPassword() throws Exception {
        String email = uniqueEmail();
        register(email, "Demo@1234");

        MvcResult forgot = mockMvc.perform(post("/auth/password/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("method", "email", "contact", email))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resetId").isNotEmpty())
                .andReturn();
        String resetId = objectMapper.readTree(forgot.getResponse().getContentAsString()).get("resetId").asText();

        MvcResult verified = mockMvc.perform(post("/auth/password/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("resetId", resetId, "code", "123456"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resetToken").isNotEmpty())
                .andReturn();
        String resetToken = objectMapper.readTree(verified.getResponse().getContentAsString()).get("resetToken").asText();

        mockMvc.perform(post("/auth/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("resetToken", resetToken, "newPassword", "Nuevo@1234"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", "Nuevo@1234"))))
                .andExpect(status().isOk());
    }

    @Test
    void passwordResetRejectsWrongCodeAndLocksAfterMaxAttempts() throws Exception {
        String email = uniqueEmail();
        register(email, "Demo@1234");
        String resetId = requestReset(email);

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/auth/password/verify-code")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(Map.of("resetId", resetId, "code", "000000"))))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/auth/password/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("resetId", resetId, "code", "123456"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void twoFactorUserDoesNotReceiveFinalSessionBeforeVerification() throws Exception {
        String email = uniqueEmail();
        AuthPayload session = register(email, "Demo@1234");

        mockMvc.perform(patch("/auth/account/2fa")
                        .header("Authorization", "Bearer " + session.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("enabled", true, "method", "email"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.twoFAEnabled").value(true));

        MvcResult challenge = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", "Demo@1234"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requires2FA").value(true))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.challengeId").isNotEmpty())
                .andReturn();
        String challengeId = objectMapper.readTree(challenge.getResponse().getContentAsString()).get("challengeId").asText();

        mockMvc.perform(post("/auth/2fa/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("challengeId", challengeId, "code", "123456"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.requires2FA").value(false));
    }

    private AuthPayload register(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", "Padre Demo",
                                "email", email,
                                "password", password,
                                "phone", "+57 300 123 4567"))))
                .andExpect(status().isCreated())
                .andReturn();
        return readAuth(result);
    }

    private String requestReset(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/password/forgot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("method", "email", "contact", email))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("resetId").asText();
    }

    private AuthPayload readAuth(MvcResult result) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return new AuthPayload(body.get("token").asText(), body.get("refreshToken").asText());
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private String uniqueEmail() {
        return "guardian-" + UUID.randomUUID() + "@example.com";
    }

    private record AuthPayload(String token, String refreshToken) {
    }

    @TestConfiguration
    static class DeterministicCodeConfig {

        @Bean
        @Primary
        SecurityCodeGenerator deterministicSecurityCodeGenerator() {
            AtomicInteger counter = new AtomicInteger();
            return new SecurityCodeGenerator() {
                @Override
                public String sixDigitCode() {
                    return "123456";
                }

                @Override
                public String opaqueToken() {
                    return "test-token-" + counter.incrementAndGet() + "-" + UUID.randomUUID();
                }
            };
        }
    }
}
