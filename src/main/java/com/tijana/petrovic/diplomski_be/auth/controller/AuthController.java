package com.tijana.petrovic.diplomski_be.auth.controller;

import com.tijana.petrovic.diplomski_be.auth.dto.LoginResponse;
import com.tijana.petrovic.diplomski_be.user.dto.ActivateAccountRequest;
import com.tijana.petrovic.diplomski_be.auth.dto.LoginRequest;
import com.tijana.petrovic.diplomski_be.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public LoginResponse login(@RequestBody LoginRequest request) {

       return authService.login(request.email(), request.password());
    }

    @GetMapping("/me")
    public String me(Authentication authentication) {
        return "Hello " + authentication.getName();
    }

}
