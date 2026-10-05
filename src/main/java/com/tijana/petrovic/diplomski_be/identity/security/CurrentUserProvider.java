package com.tijana.petrovic.diplomski_be.identity.security;

import com.tijana.petrovic.diplomski_be.identity.entity.User;
import com.tijana.petrovic.diplomski_be.identity.exception.NoAuthenticatedUserException;
import com.tijana.petrovic.diplomski_be.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Resolves the caller of the current request.
 * <p>
 * The access token carries only the email as its subject - deliberately, so that stale
 * permissions can never be cached in a token - which is why the id costs a lookup here
 * instead of being read off the principal.
 */
@RequiredArgsConstructor
@Component
public class CurrentUserProvider {

    private final UserRepository userRepository;

    public User currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NoAuthenticatedUserException("No authenticated user in the security context.");
        }

        var email = switch (authentication.getPrincipal()) {
            case UserDetails userDetails -> userDetails.getUsername();
            case String principal -> principal;
            default -> throw new NoAuthenticatedUserException("Unsupported authentication principal.");
        };

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoAuthenticatedUserException(
                        "The authenticated user no longer exists."));
    }

    public UUID currentUserId() {
        return currentUser().getId();
    }

}
