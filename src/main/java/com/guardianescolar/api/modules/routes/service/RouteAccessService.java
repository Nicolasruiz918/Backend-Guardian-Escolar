package com.guardianescolar.api.modules.routes.service;

import com.guardianescolar.api.modules.auth.service.CurrentUserService;
import com.guardianescolar.api.modules.routes.domain.Route;
import com.guardianescolar.api.modules.security.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteAccessService {

    private final CurrentUserService currentUserService;

    public void validateAccess(Route route, User current) {
        if (currentUserService.isAdmin(current) || route.getCreatedBy() == null) {
            return;
        }
        if (!route.getCreatedBy().getId().equals(current.getId())) {
            throw new AccessDeniedException("You do not have access to this route");
        }
    }
}
