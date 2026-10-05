package com.tijana.petrovic.diplomski_be.document.service;

import java.time.OffsetDateTime;

public record PresignedUpload(
        String url,
        OffsetDateTime expiresAt
) {
}
