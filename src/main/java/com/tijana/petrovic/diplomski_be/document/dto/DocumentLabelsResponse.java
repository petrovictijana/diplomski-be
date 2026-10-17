package com.tijana.petrovic.diplomski_be.document.dto;

import com.tijana.petrovic.diplomski_be.document.entity.Document;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * The document's labels after an update, so the caller sees the set the server settled on
 * (canonical, deduplicated) rather than echoing back its own request.
 */
public record DocumentLabelsResponse(
        UUID documentId,
        List<String> labels,
        OffsetDateTime updatedAt
) {
    public static DocumentLabelsResponse of(Document document, List<String> labels) {
        return new DocumentLabelsResponse(document.getId(), labels, document.getUpdatedAt());
    }
}
