package ru.tolstov.country.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ru.tolstov.country.domain.Country;
import ru.tolstov.country.domain.CountryInput;
import ru.tolstov.country.domain.CountryUpdateInput;

import java.util.List;
import java.util.UUID;

public interface CountryService {

    Country getCountry(UUID id);

    Page<Country> allCountry(Pageable pageable);

    Country addCountry(CountryInput country);

    Country updateCountryName(CountryUpdateInput country);

    List<Country> allCountriesGql();

    int addBatch(List<CountryInput> countries);

}
