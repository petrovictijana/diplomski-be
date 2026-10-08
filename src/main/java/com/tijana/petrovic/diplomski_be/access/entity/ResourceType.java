package com.tijana.petrovic.diplomski_be.access.entity;

/**
 * The kind of thing a {@link PermissionGrant} applies to.
 * <p>
 * {@code SYSTEM} covers actions with no concrete target (for example creating a group);
 * such grants always carry a {@code null} resource id.
 */
public enum ResourceType {
    DOCUMENT,
    LABEL,
    GROUP,
    SYSTEM
}
