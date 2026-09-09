package com.guardianescolar.api.modules.auth.service;

public interface SecurityCodeGenerator {

    String sixDigitCode();

    String opaqueToken();
}
