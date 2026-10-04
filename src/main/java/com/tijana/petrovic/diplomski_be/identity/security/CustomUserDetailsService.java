package com.tijana.petrovic.diplomski_be.identity.security;

import com.tijana.petrovic.diplomski_be.identity.repository.UserRepository;
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
        // UsernameNotFoundException (not a custom one) lets Spring Security answer with a generic
        // "Bad credentials", so the response does not reveal whether the email exists.
        var user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        // Invited users have no password until they activate the account
        var passwordHash = user.getPasswordHash() != null ? user.getPasswordHash() : "";

        return User.withUsername(user.getEmail())
                .password(passwordHash)
                .disabled(!user.isActive())
                .build();
    }

}
