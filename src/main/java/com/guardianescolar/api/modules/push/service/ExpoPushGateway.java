package com.guardianescolar.api.modules.push.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ExpoPushGateway implements PushGateway {

    private final PushProperties properties;
    private final ObjectMapper objectMapper;

    public ExpoPushGateway(PushProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public void send(PushMessage message) {
        if (!"expo".equalsIgnoreCase(properties.getProvider())) {
            throw new PushDeliveryException("Unsupported push provider: " + properties.getProvider(), false);
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("to", message.token());
        payload.put("title", message.title());
        payload.put("body", message.body());
        payload.put("sound", "default");
        payload.put("data", message.data());

        HttpRequest.Builder request = HttpRequest.newBuilder(properties.getExpoEndpoint())
                .timeout(properties.getRequestTimeout())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json(payload)));
        if (properties.getExpoAccessToken() != null && !properties.getExpoAccessToken().isBlank()) {
            request.header("Authorization", "Bearer " + properties.getExpoAccessToken().trim());
        }

        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(properties.getConnectTimeout())
                    .build();
            HttpResponse<String> response = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new PushDeliveryException("Expo push request failed with status " + response.statusCode(), false);
            }
            validateExpoTicket(response.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new PushDeliveryException("Expo push request interrupted", exception);
        } catch (IOException exception) {
            throw new PushDeliveryException("Expo push request failed", exception);
        }
    }

    private void validateExpoTicket(String responseBody) {
        try {
            JsonNode data = objectMapper.readTree(responseBody).path("data");
            JsonNode ticket = data.isArray() && !data.isEmpty() ? data.get(0) : data;
            if ("error".equalsIgnoreCase(ticket.path("status").asText())) {
                String errorCode = ticket.path("details").path("error").asText();
                String message = ticket.path("message").asText("Expo rejected push notification");
                throw new PushDeliveryException(message, "DeviceNotRegistered".equalsIgnoreCase(errorCode));
            }
        } catch (JsonProcessingException exception) {
            throw new PushDeliveryException("Invalid Expo push response", exception);
        }
    }

    private String json(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new PushDeliveryException("Could not serialize push payload", exception);
        }
    }
}
