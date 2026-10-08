package com.tijana.petrovic.diplomski_be.common.exception;

import com.tijana.petrovic.diplomski_be.document.exception.UnknownLabelException;
import com.tijana.petrovic.diplomski_be.identity.exception.ActiveUserAlreadyExistsException;
import com.tijana.petrovic.diplomski_be.identity.exception.InvalidCredentialsException;
import com.tijana.petrovic.diplomski_be.identity.exception.UserNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Isolated test of {@link GlobalExceptionHandler}: a tiny controller throws representative
 * exceptions through the advice via {@code standaloneSetup}, so no Spring context, security,
 * Flyway, or database is involved. One case per HTTP status plus the unexpected-error fallback.
 */
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void mapsNotFoundToUserNotFoundCode() throws Exception {
        mockMvc.perform(get("/test/user-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("no user"));
    }

    @Test
    void mapsConflictToUserAlreadyActiveCode() throws Exception {
        mockMvc.perform(get("/test/active-user-exists"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USER_ALREADY_ACTIVE"))
                .andExpect(jsonPath("$.message").value("already active"));
    }

    @Test
    void mapsUnauthorizedToInvalidCredentialsCode() throws Exception {
        mockMvc.perform(get("/test/invalid-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("bad login"));
    }

    @Test
    void mapsBadRequestToUnknownLabelCode() throws Exception {
        mockMvc.perform(get("/test/unknown-label"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("UNKNOWN_LABEL"))
                .andExpect(jsonPath("$.message").value("no such label"));
    }

    @Test
    void mapsBeanValidationFailureToValidationFailedWithFieldErrors() throws Exception {
        var body = """
                { "name": "", "email": "not-an-email" }
                """;

        mockMvc.perform(post("/test/validate").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Request validation failed."))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void mapsMalformedJsonToMalformedRequest() throws Exception {
        mockMvc.perform(post("/test/validate").contentType(MediaType.APPLICATION_JSON).content("{ not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"))
                .andExpect(jsonPath("$.message").value("Request body is missing or malformed."))
                .andExpect(jsonPath("$.fieldErrors").doesNotExist());
    }

    @Test
    void mapsUnhandledExceptionToInternalErrorWithoutLeakingMessage() throws Exception {
        mockMvc.perform(get("/test/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred."));
    }

    @RestController
    static class ThrowingController {

        @GetMapping("/test/user-not-found")
        void userNotFound() {
            throw new UserNotFoundException("no user");
        }

        @GetMapping("/test/active-user-exists")
        void activeUserExists() {
            throw new ActiveUserAlreadyExistsException("already active");
        }

        @GetMapping("/test/invalid-credentials")
        void invalidCredentials() {
            throw new InvalidCredentialsException("bad login");
        }

        @GetMapping("/test/unknown-label")
        void unknownLabel() {
            throw new UnknownLabelException("no such label");
        }

        @GetMapping("/test/boom")
        void boom() {
            throw new RuntimeException("internal detail that must not leak");
        }

        @PostMapping("/test/validate")
        void validate(@Valid @RequestBody SampleRequest request) {
            // reaching here means validation passed
        }
    }

    record SampleRequest(
            @NotBlank String name,
            @Email String email
    ) { }
}
