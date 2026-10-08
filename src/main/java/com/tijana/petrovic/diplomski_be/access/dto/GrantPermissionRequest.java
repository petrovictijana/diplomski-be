package com.tijana.petrovic.diplomski_be.access.dto;

import com.tijana.petrovic.diplomski_be.access.entity.Action;
import com.tijana.petrovic.diplomski_be.access.entity.ResourceType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * A request to grant one right.
 * <p>
 * The subject is exactly one of {@code userId} / {@code groupId}, and {@code resourceId} is
 * {@code null} for a type-wide grant - rules checked in the service, since they span fields.
 * Bean Validation only guards the two always-required fields here.
 */
public record GrantPermissionRequest(
        UUID userId,
        UUID groupId,
        @NotNull Action action,
        @NotNull ResourceType resourceType,
        UUID resourceId
) {
}
