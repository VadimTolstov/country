package ru.tolstov.contry.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.tolstov.contry.domain.Country;
import ru.tolstov.contry.service.CountryService;

import java.util.UUID;

@Controller
public class CountryQueryController {

    private final CountryService countryService;

    @Autowired
    public CountryQueryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @QueryMapping
    @ResponseStatus(HttpStatus.OK)
    public Slice<Country> countries(@Argument int page, @Argument int size) {

        return countryService.allCountry(
                PageRequest.of(
                        page,
                        size
                )
        );
    }

    @QueryMapping
    @ResponseStatus(HttpStatus.OK)
    public Country country(@Argument UUID id) {
        return countryService.getCountry(id);
    }
}
