package com.tijana.petrovic.diplomski_be.document.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Emitted when a document's label set changes. Carries the ids of the new, complete set so the
 * ingestion job can re-gate the content against the new classification; the content itself has
 * not moved, so no storage location is needed.
 */
public record DocumentLabelsChangedBody(
        UUID documentId,
        List<UUID> labelIds,
        OffsetDateTime occurredAt
) implements DocumentEvent {
}
