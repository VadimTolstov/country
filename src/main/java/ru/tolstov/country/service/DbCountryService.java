package ru.tolstov.country.service;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.tolstov.country.data.CountryEntity;
import ru.tolstov.country.data.CountryRepository;
import ru.tolstov.country.domain.Country;
import ru.tolstov.country.domain.CountryInput;
import ru.tolstov.country.domain.CountryUpdateInput;
import ru.tolstov.country.ex.CountryNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
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
                .map(ce -> new Country(
                        ce.getId(),
                        ce.getName(),
                        ce.getCode()
                ));
    }

    @Override
    public List<Country> allCountriesGql() {
        return countryRepository.findAll()
                .stream()
                .map(countryEntity ->
                        new Country(
                                countryEntity.getId(),
                                countryEntity.getName(),
                                countryEntity.getCode()
                        )
                )
                .toList();
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

    @Override
    public int addBatch(List<CountryInput> countries) {
        List<CountryEntity> entities = new ArrayList<>();
        int savedCount = 0;

        for (CountryInput input : countries) {
            try {
                // Проверка уникальности кода перед добавлением
                if (countryRepository.existsByCode(input.code())) {
                    log.warn("Duplicate country code: {}", input.code());
                    continue;
                }

                CountryEntity entity = new CountryEntity()
                        .setName(input.name())
                        .setCode(input.code());

                countryRepository.save(entity);
                savedCount++;
            } catch (Exception e) {
                log.error("Error saving country: {}", input, e);
            }
        }
        return savedCount;
    }
}
