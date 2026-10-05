package com.tijana.petrovic.diplomski_be.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateLabelRequest(

        @NotBlank(message = "Label name is required.")
        @Size(min = 2, max = 100, message = "Label name must be between 2 and 100 characters.")
        @Pattern(
                regexp = CreateLabelRequest.NAME_PATTERN,
                message = "Label name must start with a letter and may contain only letters, digits, "
                        + "and the separators _ - : between them."
        )
        String name

) {
    /**
     * Letters and digits with {@code _ - :} allowed only between them, so a separator can
     * never lead, trail or repeat - otherwise HR_PAYROLL and HR__PAYROLL would be two
     * labels no human tells apart. ASCII only: a label ends up in URLs and in SQL filters,
     * and {@code upper()} on non-ASCII is collation dependent, which would make the
     * uppercase check constraint behave differently per database.
     * <p>
     * Case is not enforced here - the request may use any case and the service normalizes it.
     */
    static final String NAME_PATTERN = "^[A-Za-z][A-Za-z0-9]*([_:-][A-Za-z0-9]+)*$";
}
