package com.tijana.petrovic.diplomski_be.access.entity;

import java.util.Set;

/**
 * A capability that can be granted. A pure verb - what it applies to is carried by the
 * {@link ResourceType} on the grant, not baked into the name, so {@code READ} over
 * {@code DOCUMENT} reads one document while {@code READ} over {@code LABEL} reads every
 * document carrying that label.
 * <p>
 * An enum rather than a table (unlike {@code Label}): every action implies enforcement logic
 * in code, so a new one cannot be added without a deployment. Each constant declares the
 * resource types it is valid for - the single source of truth for validating a grant.
 */
public enum Action {

    /** Read a single document, or every document carrying a label. */
    READ(ResourceType.DOCUMENT, ResourceType.LABEL),

    /** Apply a label to documents - scoped to a given label, or to any when the resource id is null. */
    ADD_LABEL(ResourceType.LABEL),

    /** Add members to a group - scoped to a given group, or to any when the resource id is null. */
    ADD_USER(ResourceType.GROUP),

    /** Create a new group. A system-wide capability with no concrete target. */
    CREATE_GROUP(ResourceType.SYSTEM);

    private final Set<ResourceType> resourceTypes;

    Action(ResourceType... resourceTypes) {
        this.resourceTypes = Set.of(resourceTypes);
    }

    /** Whether this action may be granted over the given resource type. */
    public boolean appliesTo(ResourceType resourceType) {
        return resourceTypes.contains(resourceType);
    }

    public Set<ResourceType> resourceTypes() {
        return resourceTypes;
    }
}
