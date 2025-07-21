package ru.tolstov.country.domain;

import java.util.UUID;

public record CountryUpdateInput(

        UUID id,

        String name,

        String code
) {
}
