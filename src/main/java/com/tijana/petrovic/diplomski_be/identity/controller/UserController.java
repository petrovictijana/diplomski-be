package com.tijana.petrovic.diplomski_be.identity.controller;

import com.tijana.petrovic.diplomski_be.identity.dto.CreateUserRequest;
import com.tijana.petrovic.diplomski_be.notification.service.EmailService;
import com.tijana.petrovic.diplomski_be.identity.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) throws MessagingException {
        userService.createUser(request);
        return ResponseEntity.ok().build();
    }

}
