package com.tijana.petrovic.diplomski_be.identity.service;

/**
 * Internal result of login/refresh. The controller decides how each token is delivered
 * (access token in the body, refresh token in an HttpOnly cookie).
 */
public record AuthTokens(
        String accessToken,
        long accessTokenExpiresIn,
        String refreshToken
) {
}
