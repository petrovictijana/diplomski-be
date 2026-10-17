package com.tijana.petrovic.diplomski_be.document.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DocumentUploadedBody.class, name = DocumentEvent.DOCUMENT_UPLOADED),
        @JsonSubTypes.Type(value = DocumentLabelsChangedBody.class, name = DocumentEvent.DOCUMENT_LABELS_CHANGED)
})
public sealed interface DocumentEvent permits DocumentUploadedBody, DocumentLabelsChangedBody {

    String DOCUMENT_UPLOADED = "DOCUMENT_UPLOADED";
    String DOCUMENT_LABELS_CHANGED = "DOCUMENT_LABELS_CHANGED";

    UUID documentId();

    OffsetDateTime occurredAt();

    /**
     * The discriminator value, for logging. Jackson writes its own copy via {@link JsonTypeInfo},
     * so this is ignored for serialization to avoid emitting the field twice.
     */
    @JsonIgnore
    default String eventType() {
        return switch (this) {
            case DocumentUploadedBody ignored -> DOCUMENT_UPLOADED;
            case DocumentLabelsChangedBody ignored -> DOCUMENT_LABELS_CHANGED;
        };
    }

}
