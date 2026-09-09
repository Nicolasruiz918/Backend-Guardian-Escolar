package com.guardianescolar.api.modules.auth.repository;

import com.guardianescolar.api.modules.auth.domain.RefreshToken;
import com.guardianescolar.api.modules.auth.domain.UserAccount;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update RefreshToken token set token.revokedAt = :now where token.user = :user and token.revokedAt is null")
    void revokeActiveByUser(UserAccount user, Instant now);
}
