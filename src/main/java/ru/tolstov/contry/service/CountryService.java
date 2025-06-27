package ru.tolstov.contry.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ru.tolstov.contry.domain.Country;
import ru.tolstov.contry.domain.CountryInput;
import ru.tolstov.contry.domain.CountryUpdateInput;

import java.util.List;
import java.util.UUID;

public interface CountryService {

    Country getCountry(UUID id);
    Slice<Country> allCountry(Pageable pageable);

    Country addCountry(CountryInput country);

    Country updateCountryName(CountryUpdateInput country);
}
