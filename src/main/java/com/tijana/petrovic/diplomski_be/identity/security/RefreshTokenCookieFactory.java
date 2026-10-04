package com.tijana.petrovic.diplomski_be.identity.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * HttpOnly  - not readable from JavaScript, so an XSS attack cannot steal it.
 * Secure    - only sent over HTTPS.
 * SameSite  - not sent on cross-site requests (CSRF protection).
 * Path      - only sent to /auth/** (refresh, logout), not with every API call.
 */
@Component
@RequiredArgsConstructor
public class RefreshTokenCookieFactory {

    private final JwtProperties jwtProperties;

    public String getCookieName() {
        return jwtProperties.getRefreshTokenCookie().getName();
    }

    public ResponseCookie create(String refreshToken) {
        return build(refreshToken, jwtProperties.getRefreshTokenExpiration());
    }

    public ResponseCookie clear() {
        return build("", Duration.ZERO);
    }

    private ResponseCookie build(String value, Duration maxAge) {
        var cookie = jwtProperties.getRefreshTokenCookie();

        return ResponseCookie.from(cookie.getName(), value)
                .httpOnly(true)
                .secure(cookie.isSecure())
                .sameSite("Strict")
                .path(cookie.getPath())
                .maxAge(maxAge)
                .build();
    }
}
