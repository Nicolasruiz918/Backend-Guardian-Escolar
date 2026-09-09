package com.guardianescolar.api.modules.auth.controller;

import com.guardianescolar.api.modules.auth.dto.AuthResponse;
import com.guardianescolar.api.modules.auth.dto.ChangePasswordRequest;
import com.guardianescolar.api.modules.auth.dto.ForgotPasswordRequest;
import com.guardianescolar.api.modules.auth.dto.ForgotPasswordResponse;
import com.guardianescolar.api.modules.auth.dto.LoginRequest;
import com.guardianescolar.api.modules.auth.dto.LogoutRequest;
import com.guardianescolar.api.modules.auth.dto.MessageResponse;
import com.guardianescolar.api.modules.auth.dto.RefreshTokenRequest;
import com.guardianescolar.api.modules.auth.dto.RegisterRequest;
import com.guardianescolar.api.modules.auth.dto.ResetPasswordRequest;
import com.guardianescolar.api.modules.auth.dto.TwoFactorSettingsRequest;
import com.guardianescolar.api.modules.auth.dto.TwoFactorVerifyRequest;
import com.guardianescolar.api.modules.auth.dto.UserSessionResponse;
import com.guardianescolar.api.modules.auth.dto.VerifyPasswordCodeRequest;
import com.guardianescolar.api.modules.auth.dto.VerifyPasswordCodeResponse;
import com.guardianescolar.api.modules.auth.service.AuthService;
import com.guardianescolar.api.shared.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/2fa/verify")
    AuthResponse verifyTwoFactor(@Valid @RequestBody TwoFactorVerifyRequest request) {
        return authService.verifyTwoFactor(request);
    }

    @PostMapping("/refresh")
    AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    MessageResponse logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.refreshToken());
        return new MessageResponse("Session closed");
    }

    @PostMapping("/password/forgot")
    ForgotPasswordResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return authService.forgotPassword(request);
    }

    @PostMapping("/password/verify-code")
    VerifyPasswordCodeResponse verifyPasswordCode(@Valid @RequestBody VerifyPasswordCodeRequest request) {
        return authService.verifyPasswordCode(request);
    }

    @PostMapping("/password/reset")
    MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return new MessageResponse("Password updated");
    }

    @PatchMapping("/account/password")
    MessageResponse changePassword(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(user.id(), request);
        return new MessageResponse("Password updated");
    }

    @PatchMapping("/account/2fa")
    UserSessionResponse configureTwoFactor(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody TwoFactorSettingsRequest request) {
        return authService.configureTwoFactor(user.id(), request);
    }
}
