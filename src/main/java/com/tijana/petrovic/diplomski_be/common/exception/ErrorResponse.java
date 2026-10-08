package com.tijana.petrovic.diplomski_be.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * Uniform error body for every failed request: a stable machine-readable {@code code}
 * the frontend can branch on, and a human-readable {@code message}.
 * <p>
 * {@code fieldErrors} is populated only for request-validation failures (field name ->
 * reason) and is omitted from the JSON otherwise, so the common shape stays {@code { code,
 * message }}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String code, String message, Map<String, String> fieldErrors) {

    public static ErrorResponse of(ErrorCode code, String message) {
        return new ErrorResponse(code.name(), message, null);
    }

    public static ErrorResponse of(ErrorCode code, String message, Map<String, String> fieldErrors) {
        return new ErrorResponse(code.name(), message, fieldErrors);
    }
}
