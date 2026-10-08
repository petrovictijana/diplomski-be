package com.tijana.petrovic.diplomski_be.access.repository;

import com.tijana.petrovic.diplomski_be.access.entity.PermissionGrant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PermissionGrantRepository extends JpaRepository<PermissionGrant, UUID> {

    /** Active rights granted directly to a user. */
    List<PermissionGrant> findByUserIdAndRevokedAtIsNull(UUID userId);

    /** Active rights granted to any of the given groups - the user's group-inherited rights. */
    List<PermissionGrant> findByGroupIdInAndRevokedAtIsNull(Collection<UUID> groupIds);
}
