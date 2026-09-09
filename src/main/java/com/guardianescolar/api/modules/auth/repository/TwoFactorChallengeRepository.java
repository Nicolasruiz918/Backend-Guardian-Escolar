package com.guardianescolar.api.modules.auth.repository;

import com.guardianescolar.api.modules.auth.domain.TwoFactorChallenge;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TwoFactorChallengeRepository extends JpaRepository<TwoFactorChallenge, UUID> {
}
