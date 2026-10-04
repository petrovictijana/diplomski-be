package com.tijana.petrovic.diplomski_be.identity.service;

import com.tijana.petrovic.diplomski_be.identity.entity.RefreshToken;
import com.tijana.petrovic.diplomski_be.identity.entity.User;
import com.tijana.petrovic.diplomski_be.identity.exception.InvalidRefreshTokenException;
import com.tijana.petrovic.diplomski_be.identity.repository.RefreshTokenRepository;
import com.tijana.petrovic.diplomski_be.identity.security.JwtProperties;
import com.tijana.petrovic.diplomski_be.identity.security.SecureTokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Refresh tokens are opaque random strings; only their SHA-256 hash is stored.
 * <p>
 * Every refresh rotates the token: the presented token is revoked and a new one is issued
 * in the same family. Presenting an already revoked token means it was used twice, i.e. it
 * was stolen, so the whole family (login session) is revoked.
 */
@Log4j2
@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private static final String INVALID_TOKEN_MESSAGE = "Refresh token is invalid or expired.";

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public record RotatedRefreshToken(String rawToken, String email) {
    }

    /**
     * Starts a new session (family) on login.
     */
    @Transactional
    public String issue(User user) {
        return create(user, UUID.randomUUID());
    }

    /**
     * noRollbackFor: when reuse is detected we revoke the family and then throw.
     * Without it the exception would roll the revocation back.
     */
    @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
    public RotatedRefreshToken rotate(String rawToken) {
        var current = findByRawToken(rawToken);
        var now = OffsetDateTime.now();

        if (current.isRevoked()) {
            log.warn("Refresh token reuse detected for family {} - revoking the whole family", current.getFamilyId());
            refreshTokenRepository.revokeFamily(current.getFamilyId(), now);
            throw new InvalidRefreshTokenException(INVALID_TOKEN_MESSAGE);
        }

        if (current.isExpired()) {
            throw new InvalidRefreshTokenException(INVALID_TOKEN_MESSAGE);
        }

        var user = current.getUser();
        if (!user.isActive()) {
            refreshTokenRepository.revokeFamily(current.getFamilyId(), now);
            throw new InvalidRefreshTokenException(INVALID_TOKEN_MESSAGE);
        }

        current.setRevokedAt(now);
        var newRawToken = create(user, current.getFamilyId());

        return new RotatedRefreshToken(newRawToken, user.getEmail());
    }

    /**
     * Logout from the current device: revokes only this session's family.
     * Unknown tokens are ignored, logout should always succeed.
     */
    @Transactional
    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(SecureTokenGenerator.hashToken(rawToken))
                .ifPresent(token -> refreshTokenRepository.revokeFamily(token.getFamilyId(), OffsetDateTime.now()));
    }

    /**
     * Expired tokens are useless, but revoked ones are kept until they expire
     * so that reuse of a rotated token can still be detected.
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredTokens() {
        var deleted = refreshTokenRepository.deleteExpiredBefore(OffsetDateTime.now());
        log.info("Deleted {} expired refresh tokens", deleted);
    }

    private String create(User user, UUID familyId) {
        var rawToken = SecureTokenGenerator.generateToken();

        var refreshToken = RefreshToken.builder()
                .user(user)
                .familyId(familyId)
                .tokenHash(SecureTokenGenerator.hashToken(rawToken))
                .expiresAt(OffsetDateTime.now().plus(jwtProperties.getRefreshTokenExpiration()))
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    private RefreshToken findByRawToken(String rawToken) {
        return refreshTokenRepository.findByTokenHash(SecureTokenGenerator.hashToken(rawToken))
                .orElseThrow(() -> new InvalidRefreshTokenException(INVALID_TOKEN_MESSAGE));
    }
}
