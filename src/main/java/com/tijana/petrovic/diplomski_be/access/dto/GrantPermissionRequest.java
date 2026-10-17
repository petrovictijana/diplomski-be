package com.tijana.petrovic.diplomski_be.access.dto;

import com.tijana.petrovic.diplomski_be.access.entity.Action;
import com.tijana.petrovic.diplomski_be.access.entity.ResourceType;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.util.UUID;

/**
 * A request to grant one right.
 * <p>
 * The subject is exactly one of {@code userId} / {@code groupId}, and {@code resourceId} is
 * {@code null} for a type-wide grant - rules checked in the service, since they span fields.
 * Bean Validation only guards the two always-required fields here.
 * <p>
 * {@code ttl} is an optional ISO-8601 duration (for example {@code "PT24H"}); when present the
 * grant expires that long after it is created, and when {@code null} the grant is permanent.
 * It must be positive - checked in the service, since Bean Validation has no duration constraint.
 */
public record GrantPermissionRequest(
        UUID userId,
        UUID groupId,
        @NotNull Action action,
        @NotNull ResourceType resourceType,
        UUID resourceId,
        Duration ttl
) {
}
