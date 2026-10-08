package com.tijana.petrovic.diplomski_be.identity.repository;

import com.tijana.petrovic.diplomski_be.identity.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserGroupRepository extends JpaRepository<UserGroup, UUID> {

    boolean existsByName(String name);
}
