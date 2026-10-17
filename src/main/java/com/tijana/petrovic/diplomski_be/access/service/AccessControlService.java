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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        return permissionGrantRepository.hasPermission(userId, action, resourceType, resourceId, OffsetDateTime.now());
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
        return permissionGrantRepository.findEffectiveGrants(userId, OffsetDateTime.now());
    }

    /**
     * The labels the user may read, as an access decision rather than a list of rows: either
     * every label (a label-wide READ grant with a null resource id), or a specific set of ids.
     * <p>
     * Derived from the same effective-grant union as every other check - this filters that
     * user's own grants (a small set already narrowed to the active ones that belong to them),
     * not a table of labels, so it does not read everything into memory to filter in Java. The
     * label store expands the "all" case; this service owns only the decision.
     */
    @Transactional(readOnly = true)
    public ReadableLabels getReadableLabels(UUID userId) {
        var labelReadGrants = getEffectiveGrants(userId).stream()
                .filter(grant -> grant.getAction() == Action.READ
                        && grant.getResourceType() == ResourceType.LABEL)
                .toList();

        var labelWideRead = labelReadGrants.stream()
                .anyMatch(grant -> grant.getResourceId() == null);
        if (labelWideRead) {
            return ReadableLabels.ofAll();
        }

        var ids = labelReadGrants.stream()
                .map(PermissionGrant::getResourceId)
                .collect(Collectors.toSet());
        return ReadableLabels.ofIds(ids);
    }
}
