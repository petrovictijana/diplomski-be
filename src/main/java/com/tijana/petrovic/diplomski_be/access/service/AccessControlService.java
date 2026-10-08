package com.tijana.petrovic.diplomski_be.access.service;

import com.tijana.petrovic.diplomski_be.access.entity.Action;
import com.tijana.petrovic.diplomski_be.access.entity.PermissionGrant;
import com.tijana.petrovic.diplomski_be.access.entity.ResourceType;
import com.tijana.petrovic.diplomski_be.access.exception.PermissionDeniedException;
import com.tijana.petrovic.diplomski_be.access.repository.PermissionGrantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * The single source of truth for access decisions. A user's effective rights are the union of
 * the grants made to them directly and the grants made to any group they belong to; this service
 * is the only place that union is evaluated. No controller or repository performs its own check.
 * <p>
 * The model is grant-only: a right held through any path is held, and nothing but a revoke removes it.
 */
@Log4j2
@RequiredArgsConstructor
@Service
public class AccessControlService {

    private final PermissionGrantRepository permissionGrantRepository;

    /**
     * Whether the user may perform the action over the resource, directly or through a group.
     * {@code resourceId} is the concrete target, or {@code null} for a type-wide check
     * (for example a SYSTEM action such as creating a group).
     */
    @Transactional(readOnly = true)
    public boolean hasPermission(UUID userId, Action action, ResourceType resourceType, UUID resourceId) {
        return permissionGrantRepository.hasPermission(userId, action, resourceType, resourceId);
    }

    /**
     * Asserts the user holds the right, throwing {@link PermissionDeniedException} otherwise.
     * The guard for endpoints and services that must proceed only when access is granted.
     */
    @Transactional(readOnly = true)
    public void requirePermission(UUID userId, Action action, ResourceType resourceType, UUID resourceId) {
        if (!hasPermission(userId, action, resourceType, resourceId)) {
            log.warn("[AccessControlService] Denied {} on {} {} for user {}",
                    action, resourceType, resourceId, userId);
            throw new PermissionDeniedException(
                    "You do not have permission to %s this %s.".formatted(action, resourceType));
        }
    }

    /**
     * Every active right the user holds, direct and group-inherited, unioned in one query.
     * The building block for permission-filtered document search.
     */
    @Transactional(readOnly = true)
    public List<PermissionGrant> getEffectiveGrants(UUID userId) {
        return permissionGrantRepository.findEffectiveGrants(userId);
    }
}
