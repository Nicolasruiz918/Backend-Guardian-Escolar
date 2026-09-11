package com.guardianescolar.api.shared.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import com.guardianescolar.api.modules.security.domain.User;
import java.util.UUID;

class JwtServiceTest {

    @Test
    void generateTokenShouldSupportSecretsThatAreNotBase64Encoded() {
        JwtService jwtService = new JwtService(
                "guardian-escolar-desarRolelo-cambiar-por-secreto-de-32-bytes-minimo",
                30L);

        User User = new User();
        User.setId(UUID.randomUUID());
        User.setEmail("demo@guardian.com");
        User.setFullName("User Demo");

        String token = assertDoesNotThrow(() -> jwtService.generateToken(User));

        assertFalse(token.isBlank());
    }
}
