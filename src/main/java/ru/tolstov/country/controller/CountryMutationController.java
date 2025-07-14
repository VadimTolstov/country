package ru.tolstov.country.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.tolstov.country.domain.Country;
import ru.tolstov.country.domain.CountryInput;
import ru.tolstov.country.domain.CountryUpdateInput;
import ru.tolstov.country.service.CountryService;

@Controller
public class CountryMutationController {

    private final CountryService countryService;

    @Autowired
    public CountryMutationController(CountryService countryService) {
        this.countryService = countryService;
    }

    @MutationMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Country addCountry(@Argument CountryInput input) {
        return countryService.addCountry(input);
    }


    @MutationMapping
    @ResponseStatus(HttpStatus.OK)
    public Country updateCountry(@Argument CountryUpdateInput input) {
        return countryService.updateCountryName(input);
    }
}
