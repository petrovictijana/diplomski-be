package com.tijana.petrovic.diplomski_be.auth.service;

import com.tijana.petrovic.diplomski_be.user.exception.UserNotFoundException;
import com.tijana.petrovic.diplomski_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found: " + email));

        return User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .disabled(!user.isActive())
                .build();
    }

}
