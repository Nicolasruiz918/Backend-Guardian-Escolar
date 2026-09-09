package com.guardianescolar.api.modules.auth.dto;

import java.util.UUID;

public record ProfileResponse(UUID id, String name, String email, String phone, boolean twoFactorEnabled,
        String twoFactorMethod) {
}
