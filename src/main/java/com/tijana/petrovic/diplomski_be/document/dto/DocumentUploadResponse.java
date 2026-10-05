package com.tijana.petrovic.diplomski_be.document.dto;

import com.tijana.petrovic.diplomski_be.document.entity.Document;
import com.tijana.petrovic.diplomski_be.document.entity.DocumentStatus;
import com.tijana.petrovic.diplomski_be.document.service.PresignedUpload;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record DocumentUploadResponse(
        UUID id,
        String filename,
        DocumentStatus status,
        List<String> labels,
        OffsetDateTime createdAt,
        String uploadUrl,
        OffsetDateTime uploadUrlExpiresAt
) {
    public static DocumentUploadResponse of(Document document, List<String> labels, PresignedUpload upload) {
        return new DocumentUploadResponse(
                document.getId(),
                document.getFilename(),
                document.getStatus(),
                labels,
                document.getCreatedAt(),
                upload.url(),
                upload.expiresAt()
        );
    }
}
