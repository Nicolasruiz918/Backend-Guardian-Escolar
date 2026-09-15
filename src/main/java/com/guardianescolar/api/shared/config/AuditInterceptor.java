package com.guardianescolar.api.shared.config;

import lombok.RequiredArgsConstructor;

import com.guardianescolar.api.modules.audit.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuditInterceptor implements HandlerInterceptor {

    private static final Set<String> AUDITED_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    private final AuditService auditService;

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception) {
        if (exception == null
                && response.getStatus() < 400
                && AUDITED_METHODS.contains(request.getMethod())
                && request.getRequestURI().startsWith("/api/")) {
            auditService.registerOperation(request, response.getStatus());
        }
    }
}
