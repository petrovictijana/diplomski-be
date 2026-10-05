package com.tijana.petrovic.diplomski_be.document.dto;

import com.tijana.petrovic.diplomski_be.document.entity.Label;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LabelResponse(
        UUID id,
        String name,
        OffsetDateTime createdAt
) {
    public static LabelResponse from(Label label) {
        return new LabelResponse(label.getId(), label.getName(), label.getCreatedAt());
    }
}
