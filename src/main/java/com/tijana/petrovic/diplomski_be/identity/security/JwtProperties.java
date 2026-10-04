package com.tijana.petrovic.diplomski_be.identity.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secretKey;

    private Duration accessTokenExpiration = Duration.ofMinutes(15);

    private Duration refreshTokenExpiration = Duration.ofDays(7);

    private RefreshTokenCookie refreshTokenCookie = new RefreshTokenCookie();

    @Getter
    @Setter
    public static class RefreshTokenCookie {

        private String name = "refresh_token";

        /**
         * Must be true in production (HTTPS). Can be disabled locally for clients
         * that do not send Secure cookies over plain HTTP.
         */
        private boolean secure = true;

        /**
         * The cookie is only sent to /auth/** endpoints (refresh, logout), never to the rest of the API.
         */
        private String path = "/auth";
    }

}
