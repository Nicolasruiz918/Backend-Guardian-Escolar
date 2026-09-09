package com.guardianescolar.api.modules.auth.service;

import com.guardianescolar.api.modules.auth.domain.UserAccount;
import com.guardianescolar.api.modules.auth.domain.UserPreference;
import com.guardianescolar.api.modules.auth.dto.ProfileResponse;
import com.guardianescolar.api.modules.auth.dto.ProfileUpdateRequest;
import com.guardianescolar.api.modules.auth.dto.UserPreferenceRequest;
import com.guardianescolar.api.modules.auth.dto.UserPreferenceResponse;
import com.guardianescolar.api.modules.auth.repository.UserAccountRepository;
import com.guardianescolar.api.modules.auth.repository.UserPreferenceRepository;
import com.guardianescolar.api.shared.exception.ApiException;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileService {

    private final UserAccountRepository users;
    private final UserPreferenceRepository preferences;

    public ProfileService(UserAccountRepository users, UserPreferenceRepository preferences) {
        this.users = users;
        this.preferences = preferences;
    }

    @Transactional(readOnly = true)
    public ProfileResponse profile(UUID userId) {
        return toProfile(findUser(userId));
    }

    public ProfileResponse updateProfile(UUID userId, ProfileUpdateRequest request) {
        UserAccount user = findUser(userId);
        user.updateProfile(request.name().trim(), normalize(request.phone()));
        return toProfile(user);
    }

    public UserPreferenceResponse preferences(UUID userId) {
        return toPreference(preferences.findByUserId(userId).orElseGet(() -> preferences.save(new UserPreference(findUser(userId)))));
    }

    public UserPreferenceResponse updatePreferences(UUID userId, UserPreferenceRequest request) {
        UserPreference preference = preferences.findByUserId(userId)
                .orElseGet(() -> preferences.save(new UserPreference(findUser(userId))));
        preference.update(request.language().trim(), request.timezone().trim(), request.pushEnabled(), request.emailEnabled(),
                request.smsEnabled(), request.locationAlertsEnabled(), request.routeAlertsEnabled(), request.safeZoneAlertsEnabled());
        return toPreference(preference);
    }

    private UserAccount findUser(UUID userId) {
        return users.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private ProfileResponse toProfile(UserAccount user) {
        return new ProfileResponse(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(),
                user.isTwoFactorEnabled(), user.getTwoFactorMethod().name());
    }

    private UserPreferenceResponse toPreference(UserPreference preference) {
        return new UserPreferenceResponse(preference.getLanguage(), preference.getTimezone(), preference.isPushEnabled(),
                preference.isEmailEnabled(), preference.isSmsEnabled(), preference.isLocationAlertsEnabled(),
                preference.isRouteAlertsEnabled(), preference.isSafeZoneAlertsEnabled());
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
