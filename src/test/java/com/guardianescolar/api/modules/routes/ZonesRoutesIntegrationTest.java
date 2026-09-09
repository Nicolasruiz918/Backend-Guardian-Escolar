package com.guardianescolar.api.modules.routes;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class ZonesRoutesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createsSafeZonesAndRoutesForStudent() throws Exception {
        String token = registerAndToken();
        String studentId = createStudent(token);

        mockMvc.perform(post("/students/" + studentId + "/zones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Casa","latitude":4.711,"longitude":-74.072,"radiusMeters":120,"active":true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Casa"));

        mockMvc.perform(post("/students/" + studentId + "/routes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Casa-Colegio","originName":"Casa","destinationName":"Colegio","active":true,
                                "points":[{"latitude":4.711,"longitude":-74.072},{"latitude":4.72,"longitude":-74.08}]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.points[1].sequence").value(2));

        mockMvc.perform(get("/students/" + studentId + "/routes").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Casa-Colegio"));
    }

    private String createStudent(String token) throws Exception {
        String response = mockMvc.perform(post("/students")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"Sofia Mora","grade":"2B","age":7,"school":"Colegio","active":true}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private String registerAndToken() throws Exception {
        String email = "zr-" + UUID.randomUUID() + "@example.com";
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
                @Override public String opaqueToken() { return "zr-token-" + sequence.incrementAndGet() + "-" + UUID.randomUUID(); }
            };
        }
    }
}
