package com.tijana.petrovic.diplomski_be.identity.controller;

import com.tijana.petrovic.diplomski_be.identity.dto.ActivateAccountRequest;
import com.tijana.petrovic.diplomski_be.identity.dto.AuthResponse;
import com.tijana.petrovic.diplomski_be.identity.dto.LoginRequest;
import com.tijana.petrovic.diplomski_be.identity.exception.InvalidRefreshTokenException;
import com.tijana.petrovic.diplomski_be.identity.security.RefreshTokenCookieFactory;
import com.tijana.petrovic.diplomski_be.identity.service.AuthService;
import com.tijana.petrovic.diplomski_be.identity.service.AuthTokens;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenCookieFactory refreshTokenCookieFactory;

    @PostMapping("/activate-account")
    public ResponseEntity<String> activateAccount(@RequestBody ActivateAccountRequest request) {
        authService.activateAccount(request.getToken(), request.getPassword());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        var tokens = authService.login(request.email(), request.password());

        return toResponse(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest request) {
        var refreshToken = readRefreshToken(request);
        if (refreshToken == null) {
            throw new InvalidRefreshTokenException("Refresh token is missing.");
        }

        var tokens = authService.refresh(refreshToken);

        return toResponse(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        var refreshToken = readRefreshToken(request);
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieFactory.clear().toString())
                .build();
    }

    @GetMapping("/me")
    public String me(Authentication authentication) {
        return "Hello " + authentication.getName();
    }

    private String readRefreshToken(HttpServletRequest request) {
        var cookie = WebUtils.getCookie(request, refreshTokenCookieFactory.getCookieName());

        return cookie != null && !cookie.getValue().isBlank() ? cookie.getValue() : null;
    }

    private ResponseEntity<AuthResponse> toResponse(AuthTokens tokens) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieFactory.create(tokens.refreshToken()).toString())
                .body(AuthResponse.bearer(tokens.accessToken(), tokens.accessTokenExpiresIn()));
    }

}
