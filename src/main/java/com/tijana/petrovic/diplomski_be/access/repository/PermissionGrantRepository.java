package com.tijana.petrovic.diplomski_be.access.repository;

import com.tijana.petrovic.diplomski_be.access.entity.Action;
import com.tijana.petrovic.diplomski_be.access.entity.PermissionGrant;
import com.tijana.petrovic.diplomski_be.access.entity.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionGrantRepository extends JpaRepository<PermissionGrant, UUID> {

    /**
     * Whether the user holds the right, directly or through any of their groups.
     * A grant with a null resource id matches every resource of its type.
     * A grant counts only while active: not revoked and not past its expiry.
     */
    @Query("""
            SELECT COUNT(pg) > 0 FROM PermissionGrant pg
            WHERE pg.revokedAt IS NULL
              AND (pg.expiresAt IS NULL OR pg.expiresAt > :now)
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
                          @Param("resourceId") UUID resourceId,
                          @Param("now") OffsetDateTime now);

    /**
     * Every active right the user holds - direct grants unioned with grants to the groups
     * they belong to. The single source for computing effective permissions in one query.
     * Expired grants are excluded the same way revoked ones are.
     */
    @Query("""
            SELECT pg FROM PermissionGrant pg
            WHERE pg.revokedAt IS NULL
              AND (pg.expiresAt IS NULL OR pg.expiresAt > :now)
              AND (
                    pg.userId = :userId
                 OR pg.groupId IN (
                      SELECT gm.group.id FROM GroupMembership gm
                      WHERE gm.user.id = :userId
                    )
              )
            """)
    List<PermissionGrant> findEffectiveGrants(@Param("userId") UUID userId,
                                              @Param("now") OffsetDateTime now);

    /**
     * The non-revoked grant occupying the active-grant uniqueness slot for a subject + scope,
     * if any. Matches the tuple of {@code uq_PermissionGrant_active}, null-safe on the subject
     * and resource id. Used before a grant to decide whether an existing row is still valid
     * (reject) or has expired (revoke, then re-issue) - because the partial unique index cannot
     * itself account for expiry.
     */
    @Query("""
            SELECT pg FROM PermissionGrant pg
            WHERE pg.revokedAt IS NULL
              AND pg.action = :action
              AND pg.resourceType = :resourceType
              AND ((:userId IS NULL AND pg.userId IS NULL) OR pg.userId = :userId)
              AND ((:groupId IS NULL AND pg.groupId IS NULL) OR pg.groupId = :groupId)
              AND ((:resourceId IS NULL AND pg.resourceId IS NULL) OR pg.resourceId = :resourceId)
            """)
    Optional<PermissionGrant> findActiveMatch(@Param("userId") UUID userId,
                                              @Param("groupId") UUID groupId,
                                              @Param("action") Action action,
                                              @Param("resourceType") ResourceType resourceType,
                                              @Param("resourceId") UUID resourceId);

    /** Active grants made directly on one resource - the subjects who hold a right on it. */
    List<PermissionGrant> findByResourceTypeAndResourceIdAndRevokedAtIsNull(
            ResourceType resourceType, UUID resourceId);
}
