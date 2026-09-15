package com.guardianescolar.api.modules.audit.service;

import com.guardianescolar.api.modules.audit.domain.AuditLog;
import com.guardianescolar.api.modules.audit.repository.AuditLogRepository;
import com.guardianescolar.api.modules.security.domain.User;
import com.guardianescolar.api.modules.security.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public void registerOperation(HttpServletRequest request, int status) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUser(currentUser().orElse(null));
            auditLog.setAccion(truncate(request.getMethod() + " " + request.getRequestURI(), 255));
            auditLog.setDescription("Operacion ejecutada por API");
            auditLog.setIpOrigen(parseIp(request.getRemoteAddr()));
            auditLog.setMetadatos("{\"method\":\"" + escape(request.getMethod())
                    + "\",\"Route\":\"" + escape(request.getRequestURI())
                    + "\",\"status\":" + status + "}");
            auditLogRepository.save(auditLog);
        } catch (RuntimeException exception) {
            LOGGER.warn("No se pudo registrar auditoria", exception);
        }
    }

    private Optional<User> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return Optional.empty();
        }
        return userRepository.findByEmailIgnoreCase(authentication.getName());
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private InetAddress parseIp(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return InetAddress.getByName(value);
        } catch (UnknownHostException exception) {
            LOGGER.warn("IP de auditoria invalida: {}", value);
            return null;
        }
    }
}
