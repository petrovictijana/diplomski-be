package com.tijana.petrovic.diplomski_be.user.service;

import com.tijana.petrovic.diplomski_be.user.model.AddUserRequest;
import com.tijana.petrovic.diplomski_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserInvitationService userInvitationService;

    public void createUser(AddUserRequest request) {
    }


}
