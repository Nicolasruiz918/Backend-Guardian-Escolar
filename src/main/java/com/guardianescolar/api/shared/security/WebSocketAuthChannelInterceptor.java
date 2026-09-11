package com.guardianescolar.api.shared.security;

import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT != accessor.getCommand()) {
            return message;
        }

        String token = extractToken(accessor);
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtService.extractUsername(token));
        if (!jwtService.isTokenValid(token, userDetails)) {
            throw new AccessDeniedException("Token WebSocket inválido");
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        accessor.setUser(authentication);
        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        List<String> authorizationHeaders = accessor.getNativeHeader("Authorization");
        if (authorizationHeaders == null || authorizationHeaders.isEmpty()) {
            authorizationHeaders = accessor.getNativeHeader("authorization");
        }

        if (authorizationHeaders == null || authorizationHeaders.isEmpty()) {
            throw new AccessDeniedException("Authorization es obligatorio para WebSocket");
        }

        String authorization = authorizationHeaders.get(0);
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new AccessDeniedException("Authorization WebSocket debe usar Bearer token");
        }
        return authorization.substring(BEARER_PREFIX.length());
    }
}
