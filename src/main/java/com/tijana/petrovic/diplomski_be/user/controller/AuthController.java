package com.tijana.petrovic.diplomski_be.user.controller;

import com.tijana.petrovic.diplomski_be.user.model.ActivateAccountRequest;
import com.tijana.petrovic.diplomski_be.user.model.LoginRequest;
import com.tijana.petrovic.diplomski_be.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/activate-account")
    public ResponseEntity<String> activateAccount(@RequestBody ActivateAccountRequest request) {
        authService.activateAccount(request.getToken(), request.getPassword());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody LoginRequest request) {
        authService.login(request.getEmail(), request.getPassword());

        return ResponseEntity.ok().build();
    }

}
