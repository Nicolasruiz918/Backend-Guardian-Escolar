package com.guardianescolar.api.modules.audit.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.guardianescolar.api.modules.audit.domain.ErrorLog;
import com.guardianescolar.api.modules.audit.repository.ErrorLogRepository;
import com.guardianescolar.api.modules.security.domain.User;
import com.guardianescolar.api.modules.security.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErrorLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorLogService.class);

    private final ErrorLogRepository errorLogRepository;
    private final UserRepository userRepository;

    public void registrar(Throwable exception, HttpServletRequest request) {
        try {
            ErrorLog registro = new ErrorLog();
            registro.setUser(UserActual().orElse(null));
            registro.setTipoError(truncate(exception.getClass().getSimpleName(), 100));
            registro.setDescription(truncate(exception.getMessage(), 4000));
            registro.setTrazaError(truncate(stackTrace(exception), 4000));
            registro.setIpOrigen(parsearIp(request == null ? null : request.getRemoteAddr()));
            registro.setMetadatos(request == null ? "{}" : "{\"method\":\"" + escape(request.getMethod())
                    + "\",\"Route\":\"" + escape(request.getRequestURI()) + "\"}");
            errorLogRepository.save(registro);
        } catch (RuntimeException loggingException) {
            LOGGER.warn("No se pudo registrar error", loggingException);
        }
    }

    private Optional<User> UserActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return Optional.empty();
        }
        return userRepository.findByEmailIgnoreCase(authentication.getName());
    }

    private String stackTrace(Throwable exception) {
        StringWriter writer = new StringWriter();
        exception.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private InetAddress parsearIp(String ip) {
        if (ip == null || ip.isBlank()) {
            return null;
        }
        try {
            return InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            LOGGER.warn("No se pudo convertir la IP {} a InetAddress", ip, ex);
            return null;
        }
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
