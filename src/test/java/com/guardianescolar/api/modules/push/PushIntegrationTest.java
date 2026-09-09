package com.guardianescolar.api.modules.push;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardianescolar.api.modules.auth.service.SecurityCodeGenerator;
import com.guardianescolar.api.modules.push.service.PushGateway;
import com.guardianescolar.api.modules.push.service.PushMessage;
import com.guardianescolar.api.shared.websocket.RealtimeLocationPublisher;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest(properties = "guardian.push.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PushIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PushGateway gateway;

    @MockBean
    private RealtimeLocationPublisher publisher;

    @BeforeEach
    void resetGateway() {
        clearInvocations(gateway);
    }

    @Test
    void authenticatedUserCanRegisterUpdateListAndDeletePushDevice() throws Exception {
        String token = registerAndToken("push-device");

        String registration = mockMvc.perform(put("/push/devices")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"ExponentPushToken[device-lifecycle]","platform":"ANDROID","deviceName":"Android inicial"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.platform").value("ANDROID"))
                .andExpect(jsonPath("$.deviceName").value("Android inicial"))
                .andExpect(jsonPath("$.active").value(true))
                .andReturn().getResponse().getContentAsString();
        String deviceId = objectMapper.readTree(registration).get("id").asText();

        mockMvc.perform(put("/push/devices")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"ExponentPushToken[device-lifecycle]","platform":"IOS","deviceName":"iPhone actualizado"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deviceId))
                .andExpect(jsonPath("$.platform").value("IOS"))
                .andExpect(jsonPath("$.deviceName").value("iPhone actualizado"));

        mockMvc.perform(get("/push/devices").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(deviceId))
                .andExpect(jsonPath("$[0].token").doesNotExist());

        String otherToken = registerAndToken("push-other");
        mockMvc.perform(delete("/push/devices/" + deviceId).header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/push/devices/" + deviceId).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/push/devices").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void outsideSafeZoneSendsPushAfterBusinessNotificationIsPersisted() throws Exception {
        String token = registerAndToken("push-alert");
        registerPushDevice(token, "ExponentPushToken[alert-device]");
        String studentId = createStudent(token);
        createSafeZone(token, studentId);

        mockMvc.perform(post("/students/" + studentId + "/locations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"latitude":4.9,"longitude":-74.3,"batteryLevel":66}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/notifications").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("OUTSIDE_SAFE_ZONE"));

        verify(gateway).send(argThat(message ->
                message.token().equals("ExponentPushToken[alert-device]")
                        && message.title().equals("Alerta de zona segura")
                        && "OUTSIDE_SAFE_ZONE".equals(message.data().get("type"))
                        && studentId.equals(message.data().get("studentId"))));
    }

    @Test
    void disabledProfilePreferenceSuppressesPushDelivery() throws Exception {
        String token = registerAndToken("push-disabled");
        registerPushDevice(token, "ExponentPushToken[disabled-device]");
        String studentId = createStudent(token);
        createSafeZone(token, studentId);

        mockMvc.perform(patch("/profile/preferences")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"language":"es","timezone":"America/Bogota","pushEnabled":false,"emailEnabled":true,
                                 "smsEnabled":false,"locationAlertsEnabled":true,"routeAlertsEnabled":true,"safeZoneAlertsEnabled":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pushEnabled").value(false));

        mockMvc.perform(post("/students/" + studentId + "/locations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"latitude":4.9,"longitude":-74.3,"batteryLevel":66}
                                """))
                .andExpect(status().isCreated());

        verify(gateway, never()).send(org.mockito.ArgumentMatchers.any(PushMessage.class));
    }

    private void registerPushDevice(String token, String pushToken) throws Exception {
        mockMvc.perform(put("/push/devices")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"%s","platform":"ANDROID","deviceName":"Telefono de prueba"}
                                """.formatted(pushToken)))
                .andExpect(status().isOk());
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

    private void createSafeZone(String token, String studentId) throws Exception {
        mockMvc.perform(post("/students/" + studentId + "/zones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Colegio","latitude":4.7,"longitude":-74.0,"radiusMeters":50,"active":true}
                                """))
                .andExpect(status().isCreated());
    }

    private String registerAndToken(String prefix) throws Exception {
        String email = prefix + "-" + UUID.randomUUID() + "@example.com";
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
                @Override
                public String sixDigitCode() {
                    return "123456";
                }

                @Override
                public String opaqueToken() {
                    return "push-test-token-" + sequence.incrementAndGet() + "-" + UUID.randomUUID();
                }
            };
        }
    }
}
