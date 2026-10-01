package com.tijana.petrovic.diplomski_be.user.service;

import com.tijana.petrovic.diplomski_be.user.entity.VerificationToken;
import com.tijana.petrovic.diplomski_be.user.exception.AccountAlreadyActivatedException;
import com.tijana.petrovic.diplomski_be.user.exception.AccountNotActivatedException;
import com.tijana.petrovic.diplomski_be.user.exception.InvalidCredentialsException;
import com.tijana.petrovic.diplomski_be.user.exception.InvalidVerificationTokenException;
import com.tijana.petrovic.diplomski_be.user.repository.UserRepository;
import com.tijana.petrovic.diplomski_be.user.repository.VerificationTokenRepository;
import com.tijana.petrovic.diplomski_be.user.util.VerificationTokenGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final VerificationTokenService verificationTokenService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    public void login(String email, String password) {
        var user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("InvalidCredentialsException"));

        if (!user.isActive()) {
            throw new AccountNotActivatedException("AccountNotActivatedException");
        }

        var passwordMatches = passwordEncoder.matches(password, user.getPasswordHash());

        if (!passwordMatches) {
            throw new InvalidCredentialsException("InvalidCredentialsException");
        }
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
