package com.tijana.petrovic.diplomski_be.identity.service;

import com.tijana.petrovic.diplomski_be.identity.entity.User;
import com.tijana.petrovic.diplomski_be.identity.exception.ActiveUserAlreadyExistsException;
import com.tijana.petrovic.diplomski_be.identity.exception.InactiveUserAlreadyExistsException;
import com.tijana.petrovic.diplomski_be.identity.dto.CreateUserRequest;
import com.tijana.petrovic.diplomski_be.identity.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;

    public void createUser(CreateUserRequest request) throws MessagingException {
        var firstName = request.firstName();
        var lastName = request.lastName();
        var email = request.email();

        log.info("[UserService] TODO");
        validateEmailNotInUse(email);

        var user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();

        var createdUser = userRepository.save(user);

        verificationTokenService.createInvitation(createdUser, null);
    }

    private void validateEmailNotInUse(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    if (user.isActive()) {
                        throw new ActiveUserAlreadyExistsException("A user with this email already exists and is active.");
                    }

                    throw new InactiveUserAlreadyExistsException("A user with this email already exists but account is not activated yet.");
                });
    }

}
