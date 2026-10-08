package com.tijana.petrovic.diplomski_be.access.dto;

import com.tijana.petrovic.diplomski_be.access.entity.Action;
import com.tijana.petrovic.diplomski_be.access.entity.PermissionGrant;
import com.tijana.petrovic.diplomski_be.access.entity.ResourceType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PermissionGrantResponse(
        UUID id,
        UUID userId,
        UUID groupId,
        Action action,
        ResourceType resourceType,
        UUID resourceId,
        OffsetDateTime createdAt,
        UUID createdBy
) {
    public static PermissionGrantResponse from(PermissionGrant grant) {
        return new PermissionGrantResponse(
                grant.getId(),
                grant.getUserId(),
                grant.getGroupId(),
                grant.getAction(),
                grant.getResourceType(),
                grant.getResourceId(),
                grant.getCreatedAt(),
                grant.getCreatedBy()
        );
    }
}
