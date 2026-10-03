package com.tijana.petrovic.diplomski_be.auth.service;

import com.tijana.petrovic.diplomski_be.auth.dto.LoginResponse;
import com.tijana.petrovic.diplomski_be.user.entity.VerificationToken;
import com.tijana.petrovic.diplomski_be.user.exception.AccountAlreadyActivatedException;
import com.tijana.petrovic.diplomski_be.user.exception.InvalidVerificationTokenException;
import com.tijana.petrovic.diplomski_be.user.repository.UserRepository;
import com.tijana.petrovic.diplomski_be.user.repository.VerificationTokenRepository;
import com.tijana.petrovic.diplomski_be.user.service.VerificationTokenService;
import com.tijana.petrovic.diplomski_be.user.util.VerificationTokenGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final VerificationTokenService verificationTokenService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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

    public LoginResponse login(String email, String password) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email, password)
        );

        var accessToken = jwtService.generateAccessToken(
                (UserDetails) Objects.requireNonNull(authentication.getPrincipal())
        );

        return new LoginResponse(accessToken);
    }

    private VerificationToken findAndValidateToken(String rawToken) {
        var tokenHash = VerificationTokenGenerator.hashToken(rawToken);

        var verificationToken = verificationTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidVerificationTokenException("InvalidVerificationTokenException"));

        verificationTokenService.validateToken(verificationToken);

        return verificationToken;
    }
}
