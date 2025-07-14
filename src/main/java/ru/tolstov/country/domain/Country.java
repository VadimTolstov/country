package ru.tolstov.country.domain;

import java.util.UUID;

public record Country(
        UUID id,

        String name,

        String code
) {
}
