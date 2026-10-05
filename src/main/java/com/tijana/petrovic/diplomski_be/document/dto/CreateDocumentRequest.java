package com.tijana.petrovic.diplomski_be.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateDocumentRequest(

        @NotBlank(message = "Filename is required.")
        @Size(max = 255, message = "Filename must not exceed 255 characters.")
        String filename,

        @NotEmpty(message = "At least one label is required.")
        @Size(max = 20, message = "A document cannot carry more than 20 labels.")
        List<
                @NotBlank(message = "Label name must not be blank.")
                @Size(max = 100, message = "Label name must not exceed 100 characters.")
                String> labels

) { }
