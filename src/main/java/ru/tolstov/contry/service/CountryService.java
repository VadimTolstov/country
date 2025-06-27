package ru.tolstov.contry.service;

import ru.tolstov.contry.domain.Country;
import ru.tolstov.contry.domain.CountryInput;
import ru.tolstov.contry.domain.CountryUpdateInput;

import java.util.List;

public interface CountryService {

    List<Country> allCountry();

    Country addCountry(CountryInput country);

    Country updateCountryName(CountryUpdateInput country);
}
