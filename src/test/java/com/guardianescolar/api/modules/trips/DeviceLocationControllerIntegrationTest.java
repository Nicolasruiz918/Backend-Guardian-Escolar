package com.guardianescolar.api.modules.trips;

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
class DeviceLocationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void linksDeviceAndStoresLatestLocation() throws Exception {
        String token = registerAndToken();
        String studentId = createStudent(token);

        mockMvc.perform(post("/students/" + studentId + "/devices")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"GPS Mochila","identifier":"gps-001","active":true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identifier").value("gps-001"));

        mockMvc.perform(post("/students/" + studentId + "/locations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"deviceIdentifier":"gps-001","latitude":4.710989,"longitude":-74.072092,
                                "accuracy":8.5,"speed":12.0,"batteryLevel":88}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.latitude").value(4.710989));

        mockMvc.perform(get("/students/" + studentId + "/locations/latest")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batteryLevel").value(88));
    }

    private String createStudent(String token) throws Exception {
        String response = mockMvc.perform(post("/students")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"Daniel Ruiz","grade":"5A","age":10,"school":"Colegio Sur","active":true}
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private String registerAndToken() throws Exception {
        String email = "gps-" + UUID.randomUUID() + "@example.com";
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
                    return "gps-token-" + sequence.incrementAndGet() + "-" + UUID.randomUUID();
                }
            };
        }
    }
}
