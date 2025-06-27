package ru.tolstov.contry.service;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.tolstov.contry.data.CountryEntity;
import ru.tolstov.contry.data.CountryRepository;
import ru.tolstov.contry.domain.Country;
import ru.tolstov.contry.domain.CountryInput;
import ru.tolstov.contry.domain.CountryUpdateInput;
import ru.tolstov.contry.ex.CountryNotFoundException;

import java.util.UUID;

@Component
public class DbCountryService implements CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public DbCountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }


    @Override
    @Nonnull
    public Country getCountry(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Country id must not be null");
        }
        CountryEntity countryEntity = countryRepository
                .findById(id)
                .orElseThrow(() -> new CountryNotFoundException("Country not found with id: " + id));
        return new Country(countryEntity.getId(), countryEntity.getName(), countryEntity.getCode());
    }

    @Override
    @Nonnull
    public Page<Country> allCountry(Pageable pageable) {
        return countryRepository.findAll(pageable)
                .map(ce -> {
                    return new Country(
                            ce.getId(),
                            ce.getName(),
                            ce.getCode()
                    );
                });
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
        CountryEntity countryEntity = countryRepository.findById(country.id())
                .orElseThrow(() -> new CountryNotFoundException("Country not found with code: " + country.code()));
        if (country.name() == null && country.code() == null) {
            return new Country(countryEntity.getId(), countryEntity.getName(), countryEntity.getCode());
        }
        countryEntity.setName(country.name() != null ? country.name() : countryEntity.getName());
        countryEntity.setCode(country.code() != null ? country.code() : countryEntity.getCode());
        countryEntity = countryRepository.save(countryEntity);
        return new Country(country.id(), countryEntity.getName(), countryEntity.getCode());
    }
}
