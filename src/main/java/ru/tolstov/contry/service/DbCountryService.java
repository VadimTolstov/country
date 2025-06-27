package ru.tolstov.contry.service;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tolstov.contry.data.CountryEntity;
import ru.tolstov.contry.data.CountryRepository;
import ru.tolstov.contry.domain.Country;
import ru.tolstov.contry.domain.CountryInput;
import ru.tolstov.contry.domain.CountryUpdateInput;
import ru.tolstov.contry.ex.CountryNotFoundException;

import java.util.List;

@Component
public class DbCountryService implements CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public DbCountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    @Nonnull
    public List<Country> allCountry() {
        return countryRepository.findAll()
                .stream()
                .map(ce -> {
                    return new Country(
                            ce.getId(),
                            ce.getName(),
                            ce.getCode()
                    );
                }).toList();
    }

    @Override
    @Nonnull
    public Country addCountry(@Nonnull CountryInput country) {
        if (country.name() == null || country.code() == null) {
            throw new IllegalArgumentException("Name and code must not be null");
        }
        CountryEntity countryEntity = new CountryEntity()
                .setName(country.name())
                .setCode(country.code());
        countryEntity = countryRepository.save(countryEntity);
        return new Country(countryEntity.getId(), countryEntity.getName(), countryEntity.getCode());
    }

    @Override
    @Nonnull
    public Country updateCountryName(@Nonnull CountryUpdateInput country) {
        CountryEntity countryEntity = countryRepository.findByCode(country.code())
                .orElseThrow(() -> new CountryNotFoundException("Country not found with code: " + country.code()));
        countryEntity.setName(country.name());
        countryEntity = countryRepository.save(countryEntity);
        return new Country(country.id(), countryEntity.getName(), countryEntity.getCode());
    }
}
