package com.tijana.petrovic.diplomski_be.document.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * The event published when a document becomes ready for downstream processing.
 * <p>
 * Carries the document's identity and its effective classification so a consumer (e.g. a
 * Databricks ingestion job) can both locate the content in object storage and know which
 * labels gate it, without ever calling back into this service.
 * <p>
 * {@code eventType} distinguishes the reasons the same shape is published - an upload now,
 * a relabel or a deletion later - so a consumer can react to each differently.
 */
public record DocumentReadyMessage(
        String eventType,
        OffsetDateTime occurredAt,
        UUID documentId,
        String bucket,
        String storageKey,
        String filename,
        List<LabelRef> labels
) {

    public static final String DOCUMENT_UPLOADED = "DOCUMENT_UPLOADED";

    /** Both the id and the name: the name is readable in the lakehouse, the id survives a rename. */
    public record LabelRef(UUID id, String name) {
    }

}
