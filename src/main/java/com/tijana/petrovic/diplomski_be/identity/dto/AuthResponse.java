package com.tijana.petrovic.diplomski_be.identity.dto;

/**
 * The refresh token is intentionally not part of the body - it is sent as an HttpOnly cookie.
 *
 * @param expiresIn access token lifetime in seconds
 */
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
    public static AuthResponse bearer(String accessToken, long expiresIn) {
        return new AuthResponse(accessToken, "Bearer", expiresIn);
    }
}
