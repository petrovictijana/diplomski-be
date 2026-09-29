package com.tijana.petrovic.diplomski_be.user.service;

import com.tijana.petrovic.diplomski_be.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserInvitationService {

    public void createInvitation(User user, User createdBy) {

    }

    public boolean validate(String token) {

        return true;
    }

    public void activateAccount(String token, String password) {

    }


}
