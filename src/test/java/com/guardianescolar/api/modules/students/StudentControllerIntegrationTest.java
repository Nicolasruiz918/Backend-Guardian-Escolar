package com.guardianescolar.api.modules.students;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void managesOnlyOwnedStudents() throws Exception {
        String token = registerAndToken("student-owner-");
        String otherToken = registerAndToken("student-other-");
        String studentId = createStudent(token, "Maria Lopez");

        mockMvc.perform(get("/students").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Maria Lopez"));

        mockMvc.perform(patch("/students/" + studentId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"Maria Actualizada","grade":"4B","age":10,"school":"Colegio Norte","active":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value("4B"));

        mockMvc.perform(get("/students/" + studentId).header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/students/" + studentId).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    private String createStudent(String token, String name) throws Exception {
        String response = mockMvc.perform(post("/students")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"%s","grade":"4A","age":9,"school":"Colegio Norte","avatarUrl":"https://example.com/avatar.png",
                                "emergencyContactName":"Tia Ana","emergencyContactPhone":"+573009998877","active":true}
                                """.formatted(name)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private String registerAndToken(String prefix) throws Exception {
        String email = prefix + UUID.randomUUID() + "@example.com";
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
                    return "student-token-" + sequence.incrementAndGet() + "-" + UUID.randomUUID();
                }
            };
        }
    }
}
