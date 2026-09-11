package com.guardianescolar.api.modules.security.repository;

import com.guardianescolar.api.modules.security.domain.SecuritySettings;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecuritySettingsRepository extends JpaRepository<SecuritySettings, UUID> {

    Optional<SecuritySettings> findBySettingName(String settingName);
}
