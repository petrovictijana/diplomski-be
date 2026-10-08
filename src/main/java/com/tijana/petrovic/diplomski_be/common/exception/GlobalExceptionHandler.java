package com.tijana.petrovic.diplomski_be.common.exception;

import com.tijana.petrovic.diplomski_be.document.exception.DocumentAlreadyUploadedException;
import com.tijana.petrovic.diplomski_be.document.exception.DocumentContentNotAvailableException;
import com.tijana.petrovic.diplomski_be.document.exception.DocumentNotFoundException;
import com.tijana.petrovic.diplomski_be.document.exception.LabelAlreadyExistsException;
import com.tijana.petrovic.diplomski_be.document.exception.UnknownLabelException;
import com.tijana.petrovic.diplomski_be.access.exception.InvalidPermissionGrantException;
import com.tijana.petrovic.diplomski_be.access.exception.PermissionAlreadyGrantedException;
import com.tijana.petrovic.diplomski_be.access.exception.PermissionDeniedException;
import com.tijana.petrovic.diplomski_be.access.exception.PermissionGrantNotFoundException;
import com.tijana.petrovic.diplomski_be.identity.exception.AccountAlreadyActivatedException;
import com.tijana.petrovic.diplomski_be.identity.exception.AccountNotActivatedException;
import com.tijana.petrovic.diplomski_be.identity.exception.ActiveUserAlreadyExistsException;
import com.tijana.petrovic.diplomski_be.identity.exception.InactiveUserAlreadyExistsException;
import com.tijana.petrovic.diplomski_be.identity.exception.InvalidCredentialsException;
import com.tijana.petrovic.diplomski_be.identity.exception.InvalidRefreshTokenException;
import com.tijana.petrovic.diplomski_be.identity.exception.InvalidVerificationTokenException;
import com.tijana.petrovic.diplomski_be.identity.exception.NoAuthenticatedUserException;
import com.tijana.petrovic.diplomski_be.identity.exception.UserNotFoundException;
import com.tijana.petrovic.diplomski_be.identity.exception.VerificationTokenExpiredException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;

/**
 * Single translation point from exceptions to the uniform {@link ErrorResponse}. Each handler
 * maps one {@link ErrorCode} (which carries the HTTP status); anything unmapped becomes a 500
 * whose stack trace is logged but never sent to the client.
 * <p>
 * This advice takes precedence over the {@code @ResponseStatus} annotations some exceptions
 * still carry, so those remain only as a harmless pre-advice fallback.
 * <p>
 * Request-body validation and malformed-JSON handling (the 400s) are added in a later task.
 */
@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- identity ---

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return build(ErrorCode.USER_NOT_FOUND, ex);
    }

    @ExceptionHandler(ActiveUserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleActiveUserAlreadyExists(ActiveUserAlreadyExistsException ex) {
        return build(ErrorCode.USER_ALREADY_ACTIVE, ex);
    }

    @ExceptionHandler(InactiveUserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleInactiveUserAlreadyExists(InactiveUserAlreadyExistsException ex) {
        return build(ErrorCode.USER_INVITATION_PENDING, ex);
    }

    @ExceptionHandler(AccountAlreadyActivatedException.class)
    public ResponseEntity<ErrorResponse> handleAccountAlreadyActivated(AccountAlreadyActivatedException ex) {
        return build(ErrorCode.ACCOUNT_ALREADY_ACTIVATED, ex);
    }

    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotActivated(AccountNotActivatedException ex) {
        return build(ErrorCode.ACCOUNT_NOT_ACTIVATED, ex);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return build(ErrorCode.INVALID_CREDENTIALS, ex);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return build(ErrorCode.INVALID_REFRESH_TOKEN, ex);
    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidVerificationToken(InvalidVerificationTokenException ex) {
        return build(ErrorCode.INVALID_VERIFICATION_TOKEN, ex);
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleVerificationTokenExpired(VerificationTokenExpiredException ex) {
        return build(ErrorCode.VERIFICATION_TOKEN_EXPIRED, ex);
    }

    @ExceptionHandler(NoAuthenticatedUserException.class)
    public ResponseEntity<ErrorResponse> handleNoAuthenticatedUser(NoAuthenticatedUserException ex) {
        return build(ErrorCode.NO_AUTHENTICATED_USER, ex);
    }

    // --- document ---

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDocumentNotFound(DocumentNotFoundException ex) {
        return build(ErrorCode.DOCUMENT_NOT_FOUND, ex);
    }

    @ExceptionHandler(DocumentAlreadyUploadedException.class)
    public ResponseEntity<ErrorResponse> handleDocumentAlreadyUploaded(DocumentAlreadyUploadedException ex) {
        return build(ErrorCode.DOCUMENT_ALREADY_UPLOADED, ex);
    }

    @ExceptionHandler(DocumentContentNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleDocumentContentNotAvailable(DocumentContentNotAvailableException ex) {
        return build(ErrorCode.DOCUMENT_CONTENT_NOT_AVAILABLE, ex);
    }

    @ExceptionHandler(LabelAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleLabelAlreadyExists(LabelAlreadyExistsException ex) {
        return build(ErrorCode.LABEL_ALREADY_EXISTS, ex);
    }

    @ExceptionHandler(UnknownLabelException.class)
    public ResponseEntity<ErrorResponse> handleUnknownLabel(UnknownLabelException ex) {
        return build(ErrorCode.UNKNOWN_LABEL, ex);
    }

    // --- access ---

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ErrorResponse> handlePermissionDenied(PermissionDeniedException ex) {
        return build(ErrorCode.PERMISSION_DENIED, ex);
    }

    @ExceptionHandler(InvalidPermissionGrantException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPermissionGrant(InvalidPermissionGrantException ex) {
        return build(ErrorCode.INVALID_PERMISSION_GRANT, ex);
    }

    @ExceptionHandler(PermissionAlreadyGrantedException.class)
    public ResponseEntity<ErrorResponse> handlePermissionAlreadyGranted(PermissionAlreadyGrantedException ex) {
        return build(ErrorCode.PERMISSION_ALREADY_GRANTED, ex);
    }

    @ExceptionHandler(PermissionGrantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePermissionGrantNotFound(PermissionGrantNotFoundException ex) {
        return build(ErrorCode.PERMISSION_GRANT_NOT_FOUND, ex);
    }

    // --- request shape ---

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var fieldErrors = new LinkedHashMap<String, String>();
        for (var error : ex.getBindingResult().getFieldErrors()) {
            var message = error.getDefaultMessage() != null ? error.getDefaultMessage() : "invalid";
            // keep every failed constraint on a field, not just the first
            fieldErrors.merge(error.getField(), message, (existing, next) -> existing + "; " + next);
        }
        log.debug("Validation failed: {}", fieldErrors);

        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus())
                .body(ErrorResponse.of(ErrorCode.VALIDATION_FAILED, "Request validation failed.", fieldErrors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        // the parser message can expose internals, so it is logged but not returned
        log.debug("Malformed request body: {}", ex.getMessage());

        return ResponseEntity.status(ErrorCode.MALFORMED_REQUEST.getStatus())
                .body(ErrorResponse.of(ErrorCode.MALFORMED_REQUEST, "Request body is missing or malformed."));
    }

    // --- fallback ---

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);

        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getStatus())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, "An unexpected error occurred."));
    }

    private ResponseEntity<ErrorResponse> build(ErrorCode code, RuntimeException ex) {
        log.debug("{} -> {}: {}", ex.getClass().getSimpleName(), code, ex.getMessage());

        return ResponseEntity.status(code.getStatus())
                .body(ErrorResponse.of(code, ex.getMessage()));
    }
}
