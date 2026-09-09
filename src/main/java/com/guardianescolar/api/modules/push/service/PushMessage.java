package com.guardianescolar.api.modules.push.service;

import java.util.Map;

public record PushMessage(String token, String title, String body, Map<String, String> data) {
}
