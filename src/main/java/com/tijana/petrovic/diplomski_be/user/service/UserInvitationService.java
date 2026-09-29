package com.tijana.petrovic.diplomski_be.user.service;

import com.tijana.petrovic.diplomski_be.user.util.InvitationTokenGenerator;
import com.tijana.petrovic.diplomski_be.user.entity.User;
import com.tijana.petrovic.diplomski_be.user.entity.UserInvitation;
import com.tijana.petrovic.diplomski_be.user.repository.UserInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserInvitationService {

    private final UserInvitationRepository userInvitationRepository;
    private final InvitationTokenGenerator tokenGenerator;

    private static final int TOKEN_DURATION_IN_HOURS = 24;

    public void createInvitation(User user, UUID createdBy) {
        var token = tokenGenerator.generateToken();

        var invitation = UserInvitation.builder()
                .userId(user.getId())
                .expiresAt(getExpirationTime())
                .tokenHash(tokenGenerator.hashToken(token))
                .build();

        userInvitationRepository.save(invitation);

        //TODO: Send invitation email with token
    }

    private OffsetDateTime getExpirationTime() {
        return OffsetDateTime.now().plusHours(TOKEN_DURATION_IN_HOURS);
    }
}
