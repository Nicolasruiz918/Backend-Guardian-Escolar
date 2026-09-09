package com.guardianescolar.api.shared.exception;

public record FieldErrorResponse(
        String field,
        String message) {
}
