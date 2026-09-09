package com.guardianescolar.api.shared.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardianescolar.api.modules.auth.domain.UserAccount;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private final JwtProperties properties;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public JwtService(JwtProperties properties, ObjectMapper objectMapper, Clock clock) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    public String generateAccessToken(UserAccount user) {
        Instant now = clock.instant();
        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", user.getId().toString());
        payload.put("email", user.getEmail());
        payload.put("role", user.getRole().name());
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", now.plus(properties.accessExpiration()).getEpochSecond());
        return encode(header, payload);
    }

    public JwtClaims parse(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT structure");
            }
            String signed = parts[0] + "." + parts[1];
            String expectedSignature = sign(signed);
            if (!MessageDigestSupport.constantTimeEquals(expectedSignature, parts[2])) {
                throw new IllegalArgumentException("Invalid JWT signature");
            }
            Map<String, Object> payload = objectMapper.readValue(
                    DECODER.decode(parts[1]),
                    new TypeReference<Map<String, Object>>() {
                    });
            Instant expiresAt = Instant.ofEpochSecond(((Number) payload.get("exp")).longValue());
            if (!expiresAt.isAfter(clock.instant())) {
                throw new IllegalArgumentException("Expired JWT");
            }
            return new JwtClaims((String) payload.get("sub"), (String) payload.get("email"), (String) payload.get("role"));
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid JWT", exception);
        }
    }

    private String encode(Map<String, Object> header, Map<String, Object> payload) {
        try {
            String encodedHeader = ENCODER.encodeToString(objectMapper.writeValueAsBytes(header));
            String encodedPayload = ENCODER.encodeToString(objectMapper.writeValueAsBytes(payload));
            String signed = encodedHeader + "." + encodedPayload;
            return signed + "." + sign(signed);
        } catch (Exception exception) {
            throw new IllegalStateException("JWT generation failed", exception);
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("JWT signature failed", exception);
        }
    }

    public record JwtClaims(String subject, String email, String role) {
    }
}
