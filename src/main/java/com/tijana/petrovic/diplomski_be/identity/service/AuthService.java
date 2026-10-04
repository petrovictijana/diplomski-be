package com.tijana.petrovic.diplomski_be.identity.service;

import com.tijana.petrovic.diplomski_be.identity.entity.VerificationToken;
import com.tijana.petrovic.diplomski_be.identity.exception.AccountAlreadyActivatedException;
import com.tijana.petrovic.diplomski_be.identity.exception.InvalidVerificationTokenException;
import com.tijana.petrovic.diplomski_be.identity.exception.UserNotFoundException;
import com.tijana.petrovic.diplomski_be.identity.repository.UserRepository;
import com.tijana.petrovic.diplomski_be.identity.repository.VerificationTokenRepository;
import com.tijana.petrovic.diplomski_be.identity.security.JwtService;
import com.tijana.petrovic.diplomski_be.identity.security.SecureTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final VerificationTokenService verificationTokenService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public void activateAccount(String rawToken, String password) {
        var verificationToken = findAndValidateToken(rawToken);

        var user = verificationToken.getUser();
        if (user.isActive()) {
            throw new AccountAlreadyActivatedException("AccountAlreadyActivatedException");
        }

        var passwordHash = passwordEncoder.encode(password);

        user.setPasswordHash(passwordHash);
        user.setActive(true);

        verificationToken.setUsedAt(OffsetDateTime.now());

        userRepository.save(user);
        verificationTokenRepository.save(verificationToken);
    }

    public AuthTokens login(String email, String password) {
        // Throws AuthenticationException (-> 401) for wrong credentials or a disabled account
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        var refreshToken = refreshTokenService.issue(user);

        return createTokens(user.getEmail(), refreshToken);
    }

    public AuthTokens refresh(String rawRefreshToken) {
        var rotated = refreshTokenService.rotate(rawRefreshToken);

        return createTokens(rotated.email(), rotated.rawToken());
    }

    public void logout(String rawRefreshToken) {
        refreshTokenService.revoke(rawRefreshToken);
    }

    private AuthTokens createTokens(String email, String refreshToken) {
        return new AuthTokens(
                jwtService.generateAccessToken(email),
                jwtService.getAccessTokenExpiresInSeconds(),
                refreshToken
        );
    }

    private VerificationToken findAndValidateToken(String rawToken) {
        var tokenHash = SecureTokenGenerator.hashToken(rawToken);

        var verificationToken = verificationTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidVerificationTokenException("InvalidVerificationTokenException"));

        verificationTokenService.validateToken(verificationToken);

        return verificationToken;
    }
}
