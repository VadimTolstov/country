package ru.tolstov.contry.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

public record Country(
        UUID id,

        @NotBlank(message = "Country code is required")
        @Size(min = 2, max = 100, message = "Code must be between 2 and 5 characters")
        String name,

        @NotBlank(message = "Country name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String code
) {
}
