package com.tijana.petrovic.diplomski_be.access.service;

import com.tijana.petrovic.diplomski_be.access.dto.GrantPermissionRequest;
import com.tijana.petrovic.diplomski_be.access.dto.PermissionGrantResponse;
import com.tijana.petrovic.diplomski_be.access.entity.PermissionGrant;
import com.tijana.petrovic.diplomski_be.access.entity.ResourceType;
import com.tijana.petrovic.diplomski_be.access.exception.InvalidPermissionGrantException;
import com.tijana.petrovic.diplomski_be.access.exception.PermissionAlreadyGrantedException;
import com.tijana.petrovic.diplomski_be.access.exception.PermissionGrantNotFoundException;
import com.tijana.petrovic.diplomski_be.access.repository.PermissionGrantRepository;
import com.tijana.petrovic.diplomski_be.document.repository.DocumentRepository;
import com.tijana.petrovic.diplomski_be.document.repository.LabelRepository;
import com.tijana.petrovic.diplomski_be.identity.repository.UserGroupRepository;
import com.tijana.petrovic.diplomski_be.identity.repository.UserRepository;
import com.tijana.petrovic.diplomski_be.identity.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Grants and revokes rights. Separate from {@code AccessControlService}, which only evaluates
 * access: this service writes the grant table, that one reads it.
 * <p>
 * Every grant is validated here before it reaches the database - the unique index and check
 * constraints are the final guard, not the first. A right is withdrawn by revoking (stamping
 * {@code revokedAt}), never by deleting the row, so the audit trail survives.
 */
@Log4j2
@RequiredArgsConstructor
@Service
public class PermissionGrantService {

    private final PermissionGrantRepository permissionGrantRepository;
    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final LabelRepository labelRepository;
    private final DocumentRepository documentRepository;

    @Transactional
    public PermissionGrantResponse grant(GrantPermissionRequest request) {
        validateSubject(request);
        validateScope(request);

        var actingUserId = currentUserProvider.currentUserId();

        var grant = PermissionGrant.builder()
                .userId(request.userId())
                .groupId(request.groupId())
                .action(request.action())
                .resourceType(request.resourceType())
                .resourceId(request.resourceId())
                .createdBy(actingUserId)
                .build();

        try {
            // saveAndFlush so the unique-index violation surfaces here, not at commit
            var saved = permissionGrantRepository.saveAndFlush(grant);

            log.info("[PermissionGrantService] Granted {} on {} {} to {} by {}",
                    saved.getAction(), saved.getResourceType(), saved.getResourceId(),
                    saved.getUserId() != null ? "user " + saved.getUserId() : "group " + saved.getGroupId(),
                    actingUserId);

            return PermissionGrantResponse.from(saved);
        } catch (DataIntegrityViolationException exception) {
            // The partial unique index is the real guard against a duplicate active grant
            throw new PermissionAlreadyGrantedException("This right is already granted to the subject.");
        }
    }

    @Transactional
    public void revoke(UUID grantId) {
        var grant = permissionGrantRepository.findById(grantId)
                .filter(existing -> !existing.isRevoked())
                .orElseThrow(() -> new PermissionGrantNotFoundException(
                        "No active permission grant with id %s.".formatted(grantId)));

        grant.setRevokedAt(OffsetDateTime.now());
        grant.setRevokedBy(currentUserProvider.currentUserId());
        permissionGrantRepository.save(grant);

        log.info("[PermissionGrantService] Revoked grant {} by {}", grantId, grant.getRevokedBy());
    }

    /** Active grants made directly on a specific resource - who holds a right on it. */
    @Transactional(readOnly = true)
    public List<PermissionGrantResponse> listForResource(ResourceType resourceType, UUID resourceId) {
        return permissionGrantRepository
                .findByResourceTypeAndResourceIdAndRevokedAtIsNull(resourceType, resourceId)
                .stream()
                .map(PermissionGrantResponse::from)
                .toList();
    }

    /** Exactly one subject, and it must exist. */
    private void validateSubject(GrantPermissionRequest request) {
        var hasUser = request.userId() != null;
        var hasGroup = request.groupId() != null;

        if (hasUser == hasGroup) {
            throw new InvalidPermissionGrantException(
                    "A grant must have exactly one subject: either userId or groupId.");
        }
        if (hasUser && !userRepository.existsById(request.userId())) {
            throw new InvalidPermissionGrantException("User %s does not exist.".formatted(request.userId()));
        }
        if (hasGroup && !userGroupRepository.existsById(request.groupId())) {
            throw new InvalidPermissionGrantException("Group %s does not exist.".formatted(request.groupId()));
        }
    }

    /** The action must apply to the resource type, and a targeted resource must exist. */
    private void validateScope(GrantPermissionRequest request) {
        if (!request.action().appliesTo(request.resourceType())) {
            throw new InvalidPermissionGrantException(
                    "Action %s cannot be granted over %s.".formatted(request.action(), request.resourceType()));
        }

        if (request.resourceType() == ResourceType.SYSTEM) {
            if (request.resourceId() != null) {
                throw new InvalidPermissionGrantException("A SYSTEM grant must not target a resource.");
            }
            return;
        }

        // null resourceId is a type-wide grant ("all resources of this type") - nothing to resolve
        if (request.resourceId() == null) {
            return;
        }

        var exists = switch (request.resourceType()) {
            case DOCUMENT -> documentRepository.findByIdAndDeletedAtIsNull(request.resourceId()).isPresent();
            case LABEL -> labelRepository.existsById(request.resourceId());
            case GROUP -> userGroupRepository.existsById(request.resourceId());
            case SYSTEM -> true; // unreachable - handled above
        };
        if (!exists) {
            throw new InvalidPermissionGrantException(
                    "%s %s does not exist.".formatted(request.resourceType(), request.resourceId()));
        }
    }
}
