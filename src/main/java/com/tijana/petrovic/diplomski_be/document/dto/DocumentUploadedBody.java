package com.tijana.petrovic.diplomski_be.document.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Emitted once a document's upload is confirmed. Carries everything the ingestion job needs to
 * locate the content ({@code bucket} + {@code storageKey}) and the ids of the labels that gate
 * it, without calling back into this service. Only the ids travel: Databricks filters on them
 * and never needs the human-readable name.
 */
public record DocumentUploadedBody(
        UUID documentId,
        String bucket,
        String storageKey,
        String filename,
        List<UUID> labelIds,
        OffsetDateTime occurredAt
) implements DocumentEvent {
}
