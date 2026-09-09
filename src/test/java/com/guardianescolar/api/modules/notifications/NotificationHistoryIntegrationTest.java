package com.guardianescolar.api.modules.notifications;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardianescolar.api.modules.auth.service.SecurityCodeGenerator;
import com.guardianescolar.api.shared.websocket.RealtimeLocationPublisher;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationHistoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RealtimeLocationPublisher publisher;

    @Test
    void outsideZoneCreatesNotificationAndHistory() throws Exception {
        String token = registerAndToken();
        String studentId = createStudent(token);

        mockMvc.perform(post("/students/" + studentId + "/zones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Colegio","latitude":4.7,"longitude":-74.0,"radiusMeters":50,"active":true}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/students/" + studentId + "/locations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"latitude":4.9,"longitude":-74.3,"batteryLevel":66}
                                """))
                .andExpect(status().isCreated());

        String notifications = mockMvc.perform(get("/notifications").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("OUTSIDE_SAFE_ZONE"))
                .andReturn().getResponse().getContentAsString();
        String notificationId = objectMapper.readTree(notifications).get(0).get("id").asText();

        mockMvc.perform(patch("/notifications/" + notificationId + "/read").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readAt").exists());

        mockMvc.perform(get("/students/" + studentId + "/history").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("OUTSIDE_SAFE_ZONE"));
    }

    private String createStudent(String token) throws Exception {
        String response = mockMvc.perform(post("/students")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"Nicolas Pardo","grade":"6A","age":11,"school":"Colegio","active":true}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private String registerAndToken() throws Exception {
        String email = "nt-" + UUID.randomUUID() + "@example.com";
        String response = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Acudiente Demo","phone":"+573001234567","email":"%s","password":"Demo@1234"}
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
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
                @Override public String sixDigitCode() { return "123456"; }
                @Override public String opaqueToken() { return "nt-token-" + sequence.incrementAndGet() + "-" + UUID.randomUUID(); }
            };
        }
    }
}
