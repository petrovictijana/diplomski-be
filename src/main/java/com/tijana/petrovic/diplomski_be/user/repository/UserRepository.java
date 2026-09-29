package com.tijana.petrovic.diplomski_be.user.repository;

import com.tijana.petrovic.diplomski_be.user.entity.User;
import com.tijana.petrovic.diplomski_be.user.exception.UserNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds user by ID or throws UserNotFoundException if not found.
     *
     * @param id the UUID of the user
     * @return the user entity
     * @throws UserNotFoundException if user with given ID does not exist
     */
    default User findExistingById(UUID id) {
        return findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with ID %s not found.".formatted(id)
                ));
    }

    Optional<User> findByEmail(String email);
}
