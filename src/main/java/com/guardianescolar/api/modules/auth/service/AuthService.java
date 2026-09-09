package com.guardianescolar.api.modules.auth.service;

import com.guardianescolar.api.modules.auth.domain.RefreshToken;
import com.guardianescolar.api.modules.auth.domain.TwoFactorChallenge;
import com.guardianescolar.api.modules.auth.domain.TwoFactorMethod;
import com.guardianescolar.api.modules.auth.domain.UserAccount;
import com.guardianescolar.api.modules.auth.domain.VerificationCode;
import com.guardianescolar.api.modules.auth.domain.VerificationCodeType;
import com.guardianescolar.api.modules.auth.dto.AuthResponse;
import com.guardianescolar.api.modules.auth.dto.ChangePasswordRequest;
import com.guardianescolar.api.modules.auth.dto.ForgotPasswordRequest;
import com.guardianescolar.api.modules.auth.dto.ForgotPasswordResponse;
import com.guardianescolar.api.modules.auth.dto.LoginRequest;
import com.guardianescolar.api.modules.auth.dto.RegisterRequest;
import com.guardianescolar.api.modules.auth.dto.ResetPasswordRequest;
import com.guardianescolar.api.modules.auth.dto.TwoFactorSettingsRequest;
import com.guardianescolar.api.modules.auth.dto.TwoFactorVerifyRequest;
import com.guardianescolar.api.modules.auth.dto.UserSessionResponse;
import com.guardianescolar.api.modules.auth.dto.VerifyPasswordCodeRequest;
import com.guardianescolar.api.modules.auth.dto.VerifyPasswordCodeResponse;
import com.guardianescolar.api.modules.auth.repository.RefreshTokenRepository;
import com.guardianescolar.api.modules.auth.repository.TwoFactorChallengeRepository;
import com.guardianescolar.api.modules.auth.repository.UserAccountRepository;
import com.guardianescolar.api.modules.auth.repository.VerificationCodeRepository;
import com.guardianescolar.api.shared.exception.ApiException;
import com.guardianescolar.api.shared.security.JwtProperties;
import com.guardianescolar.api.shared.security.JwtService;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,72}$";

    private final UserAccountRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final TwoFactorChallengeRepository twoFactorChallengeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final SecurityCodeProperties codeProperties;
    private final SecurityCodeGenerator codeGenerator;
    private final CodeDeliveryService codeDeliveryService;
    private final TokenHasher tokenHasher;
    private final Clock clock;

    public AuthService(
            UserAccountRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            VerificationCodeRepository verificationCodeRepository,
            TwoFactorChallengeRepository twoFactorChallengeRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            JwtProperties jwtProperties,
            SecurityCodeProperties codeProperties,
            SecurityCodeGenerator codeGenerator,
            CodeDeliveryService codeDeliveryService,
            TokenHasher tokenHasher,
            Clock clock) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.twoFactorChallengeRepository = twoFactorChallengeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.codeProperties = codeProperties;
        this.codeGenerator = codeGenerator;
        this.codeDeliveryService = codeDeliveryService;
        this.tokenHasher = tokenHasher;
        this.clock = clock;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validatePassword(request.password());
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }
        UserAccount user = new UserAccount(
                email,
                passwordEncoder.encode(request.password()),
                request.name().trim(),
                request.phone().trim());
        userRepository.save(user);
        return createSession(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        UserAccount user = userRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .orElseThrow(() -> invalidCredentials());
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw invalidCredentials();
        }
        if (user.isTwoFactorEnabled()) {
            String code = codeGenerator.sixDigitCode();
            TwoFactorChallenge challenge = new TwoFactorChallenge(
                    user,
                    user.getTwoFactorMethod(),
                    tokenHasher.hash(code),
                    clock.instant().plus(codeProperties.expiration()),
                    codeProperties.maxAttempts());
            twoFactorChallengeRepository.save(challenge);
            codeDeliveryService.deliver(deliveryContact(user, user.getTwoFactorMethod()), user.getTwoFactorMethod(), code);
            return new AuthResponse(null, null, null, null, true, challenge.getId(),
                    user.getTwoFactorMethod().name().toLowerCase(Locale.ROOT), user.getEmail());
        }
        return createSession(user);
    }

    @Transactional(noRollbackFor = ApiException.class)
    public AuthResponse verifyTwoFactor(TwoFactorVerifyRequest request) {
        TwoFactorChallenge challenge = twoFactorChallengeRepository.findById(request.challengeId())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid two factor challenge"));
        Instant now = clock.instant();
        if (!challenge.canVerify(now)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Two factor challenge expired or locked");
        }
        challenge.registerAttempt();
        if (!tokenHasher.hash(request.code()).equals(challenge.getCodeHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid verification code");
        }
        challenge.markUsed(now);
        return createSession(challenge.getUser());
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHasher.hash(refreshToken))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
        if (!token.isActive(clock.instant())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
        token.revoke(clock.instant());
        return createSession(token.getUser());
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByTokenHash(tokenHasher.hash(refreshToken))
                .ifPresent(token -> token.revoke(clock.instant()));
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        String method = normalizeMethod(request.method()).name().toLowerCase(Locale.ROOT);
        String contact = request.contact().trim();
        UserAccount user = findByContact(method, contact);
        String code = codeGenerator.sixDigitCode();
        VerificationCode verificationCode = new VerificationCode(
                user,
                VerificationCodeType.PASSWORD_RESET,
                contact,
                tokenHasher.hash(code),
                clock.instant().plus(codeProperties.expiration()),
                codeProperties.maxAttempts());
        verificationCodeRepository.save(verificationCode);
        codeDeliveryService.deliver(contact, normalizeMethod(method), code);
        return new ForgotPasswordResponse(verificationCode.getId(), method, contact,
                (int) codeProperties.expiration().toMinutes());
    }

    @Transactional(noRollbackFor = ApiException.class)
    public VerifyPasswordCodeResponse verifyPasswordCode(VerifyPasswordCodeRequest request) {
        VerificationCode code = verificationCodeRepository
                .findByIdAndType(request.resetId(), VerificationCodeType.PASSWORD_RESET)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid verification code"));
        Instant now = clock.instant();
        if (!code.canVerify(now)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Verification code expired or locked");
        }
        code.registerAttempt();
        if (!tokenHasher.hash(request.code()).equals(code.getCodeHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid verification code");
        }
        String resetToken = codeGenerator.opaqueToken();
        code.markUsed(tokenHasher.hash(resetToken), now.plus(codeProperties.expiration()), now);
        return new VerifyPasswordCodeResponse(resetToken);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        validatePassword(request.newPassword());
        VerificationCode code = verificationCodeRepository.findByResetTokenHash(tokenHasher.hash(request.resetToken()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid reset token"));
        if (!code.canReset(clock.instant())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid reset token");
        }
        UserAccount user = code.getUser();
        user.changePassword(passwordEncoder.encode(request.newPassword()));
        refreshTokenRepository.revokeActiveByUser(user, clock.instant());
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        validatePassword(request.newPassword());
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Current password is incorrect");
        }
        user.changePassword(passwordEncoder.encode(request.newPassword()));
        refreshTokenRepository.revokeActiveByUser(user, clock.instant());
    }

    @Transactional
    public UserSessionResponse configureTwoFactor(UUID userId, TwoFactorSettingsRequest request) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        user.configureTwoFactor(request.enabled(), normalizeMethod(request.method() == null ? "email" : request.method()));
        return toUser(user);
    }

    private AuthResponse createSession(UserAccount user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = codeGenerator.opaqueToken();
        RefreshToken token = new RefreshToken(
                user,
                tokenHasher.hash(refreshToken),
                clock.instant().plus(jwtProperties.refreshExpiration()));
        refreshTokenRepository.save(token);
        return new AuthResponse(accessToken, accessToken, refreshToken, toUser(user), false, null, null, user.getEmail());
    }

    private UserSessionResponse toUser(UserAccount user) {
        return new UserSessionResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole().name(),
                user.isTwoFactorEnabled(),
                user.getTwoFactorMethod().name().toLowerCase(Locale.ROOT));
    }

    private UserAccount findByContact(String method, String contact) {
        if ("email".equals(method)) {
            return userRepository.findByEmailIgnoreCase(contact)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        }
        return userRepository.findAll()
                .stream()
                .filter(user -> contact.equals(user.getPhone()))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private String deliveryContact(UserAccount user, TwoFactorMethod method) {
        return method == TwoFactorMethod.SMS ? user.getPhone() : user.getEmail();
    }

    private TwoFactorMethod normalizeMethod(String method) {
        return switch (method.trim().toLowerCase(Locale.ROOT)) {
            case "sms", "phone" -> TwoFactorMethod.SMS;
            case "email" -> TwoFactorMethod.EMAIL;
            default -> throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported verification method");
        };
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void validatePassword(String password) {
        if (password == null || !password.matches(PASSWORD_PATTERN)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Password must include uppercase, lowercase, number and special character");
        }
    }

    private ApiException invalidCredentials() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
}
