package com.tijana.petrovic.diplomski_be.user.service;

import com.tijana.petrovic.diplomski_be.user.entity.User;
import com.tijana.petrovic.diplomski_be.user.exception.ActiveUserAlreadyExistsException;
import com.tijana.petrovic.diplomski_be.user.exception.InactiveUserAlreadyExistsException;
import com.tijana.petrovic.diplomski_be.user.model.CreateUserRequest;
import com.tijana.petrovic.diplomski_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserInvitationService userInvitationService;

    public void createUser(CreateUserRequest request) {
        var firstName = request.getFirstName();
        var lastName = request.getLastName();
        var email = request.getEmail();

        log.info("[UserService] TODO");
        validateEmailNotInUse(email);

        var user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();

        var createdUser = userRepository.save(user);

        userInvitationService.createInvitation(createdUser, null);
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
