package com.tijana.petrovic.diplomski_be.access.repository;

import com.tijana.petrovic.diplomski_be.access.entity.Action;
import com.tijana.petrovic.diplomski_be.access.entity.PermissionGrant;
import com.tijana.petrovic.diplomski_be.access.entity.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PermissionGrantRepository extends JpaRepository<PermissionGrant, UUID> {

    /**
     * Whether the user holds the right, directly or through any of their groups.
     * A grant with a null resource id matches every resource of its type.
     */
    @Query("""
            SELECT COUNT(pg) > 0 FROM PermissionGrant pg
            WHERE pg.revokedAt IS NULL
              AND pg.action = :action
              AND pg.resourceType = :resourceType
              AND (pg.resourceId IS NULL OR pg.resourceId = :resourceId)
              AND (
                    pg.userId = :userId
                 OR pg.groupId IN (
                      SELECT gm.group.id FROM GroupMembership gm
                      WHERE gm.user.id = :userId
                    )
              )
            """)
    boolean hasPermission(@Param("userId") UUID userId,
                          @Param("action") Action action,
                          @Param("resourceType") ResourceType resourceType,
                          @Param("resourceId") UUID resourceId);

    /**
     * Every active right the user holds - direct grants unioned with grants to the groups
     * they belong to. The single source for computing effective permissions in one query.
     */
    @Query("""
            SELECT pg FROM PermissionGrant pg
            WHERE pg.revokedAt IS NULL
              AND (
                    pg.userId = :userId
                 OR pg.groupId IN (
                      SELECT gm.group.id FROM GroupMembership gm
                      WHERE gm.user.id = :userId
                    )
              )
            """)
    List<PermissionGrant> findEffectiveGrants(@Param("userId") UUID userId);
}
