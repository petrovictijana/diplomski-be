package com.tijana.petrovic.diplomski_be.document.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * The complete new label set for a document - a replace, not a merge: whatever is sent
 * becomes the document's labels, and any label left out is removed.
 */
public record UpdateDocumentLabelsRequest(

        @NotEmpty(message = "At least one label is required.")
        @Size(max = 20, message = "A document cannot carry more than 20 labels.")
        List<
                @NotBlank(message = "Label name must not be blank.")
                @Size(max = 100, message = "Label name must not exceed 100 characters.")
                String> labels

) { }
