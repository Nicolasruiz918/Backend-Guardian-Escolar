package com.guardianescolar.api.modules.auth.controller;

import com.guardianescolar.api.modules.auth.dto.ProfileResponse;
import com.guardianescolar.api.modules.auth.dto.ProfileUpdateRequest;
import com.guardianescolar.api.modules.auth.dto.UserPreferenceRequest;
import com.guardianescolar.api.modules.auth.dto.UserPreferenceResponse;
import com.guardianescolar.api.modules.auth.service.ProfileService;
import com.guardianescolar.api.shared.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping
    public ProfileResponse profile(@AuthenticationPrincipal UserPrincipal principal) {
        return service.profile(principal.id());
    }

    @PatchMapping
    public ProfileResponse updateProfile(@AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProfileUpdateRequest request) {
        return service.updateProfile(principal.id(), request);
    }

    @GetMapping("/preferences")
    public UserPreferenceResponse preferences(@AuthenticationPrincipal UserPrincipal principal) {
        return service.preferences(principal.id());
    }

    @PatchMapping("/preferences")
    public UserPreferenceResponse updatePreferences(@AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserPreferenceRequest request) {
        return service.updatePreferences(principal.id(), request);
    }
}
