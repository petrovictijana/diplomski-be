package com.tijana.petrovic.diplomski_be.user.service;

import com.tijana.petrovic.diplomski_be.user.exception.InvalidVerificationTokenException;
import com.tijana.petrovic.diplomski_be.user.exception.VerificationTokenExpiredException;
import com.tijana.petrovic.diplomski_be.user.util.VerificationTokenGenerator;
import com.tijana.petrovic.diplomski_be.user.entity.User;
import com.tijana.petrovic.diplomski_be.user.entity.VerificationToken;
import com.tijana.petrovic.diplomski_be.user.repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final VerificationTokenGenerator tokenGenerator;
    private final EmailService emailService;

    private static final int TOKEN_DURATION_IN_HOURS = 24;

    public void createInvitation(User user, UUID createdBy) throws MessagingException {
        var token = tokenGenerator.generateToken();

        var invitation = VerificationToken.builder()
                .userId(user.getId())
                .expiresAt(getExpirationTime())
                .tokenHash(tokenGenerator.hashToken(token))
                .build();

        verificationTokenRepository.save(invitation);

        //TODO: Send invitation email with token
        emailService.sendInvitationEmail(user.getFirstName(), user.getEmail(), token);
    }

    public void validateToken(VerificationToken token) {
        if (token.getUsedAt() != null) {
            throw new InvalidVerificationTokenException("InvalidVerificationTokenException");
        }

        if (token.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new VerificationTokenExpiredException("VerificationTokenExpiredException");
        }
    }

    private OffsetDateTime getExpirationTime() {
        return OffsetDateTime.now().plusHours(TOKEN_DURATION_IN_HOURS);
    }
}
