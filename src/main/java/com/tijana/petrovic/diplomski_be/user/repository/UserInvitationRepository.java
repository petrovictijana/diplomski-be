package com.tijana.petrovic.diplomski_be.user.repository;

import com.tijana.petrovic.diplomski_be.user.entity.UserInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserInvitationRepository extends JpaRepository<UserInvitation, UUID> {
}
