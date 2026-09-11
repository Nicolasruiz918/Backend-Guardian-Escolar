package com.guardianescolar.api.modules.security.domain;

public final class TwoFactorMethods {

    public static final String EMAIL = "EMAIL";
    public static final String SMS = "SMS";
    public static final String REGEX = EMAIL + "|" + SMS;

    private TwoFactorMethods() {
    }
}
