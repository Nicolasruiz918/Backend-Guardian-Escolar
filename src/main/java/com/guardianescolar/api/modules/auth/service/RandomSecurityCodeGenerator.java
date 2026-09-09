package com.guardianescolar.api.modules.auth.service;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class RandomSecurityCodeGenerator implements SecurityCodeGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String sixDigitCode() {
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    @Override
    public String opaqueToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
