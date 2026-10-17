package com.tijana.petrovic.diplomski_be.access.service;

import java.util.Set;
import java.util.UUID;

/**
 * The outcome of evaluating which labels a user may read: either every label (a label-wide
 * READ grant with a null resource id), or a specific set of label ids.
 * <p>
 * The "all" case is kept explicit rather than flattened into an id list, so a label-wide grant
 * is never silently expanded here - turning it into concrete rows belongs to the label store,
 * not to the access decision.
 */
public record ReadableLabels(boolean all, Set<UUID> ids) {

    public static ReadableLabels ofAll() {
        return new ReadableLabels(true, Set.of());
    }

    public static ReadableLabels ofIds(Set<UUID> ids) {
        return new ReadableLabels(false, Set.copyOf(ids));
    }
}
