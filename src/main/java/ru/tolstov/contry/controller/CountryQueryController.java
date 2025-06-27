package ru.tolstov.contry.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.tolstov.contry.domain.Country;
import ru.tolstov.contry.service.CountryService;

import java.util.List;

@Controller
public class CountryQueryController {

    private final CountryService countryService;

    @Autowired
    public CountryQueryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @QueryMapping
    //@ResponseStatus(HttpStatus.OK)
    public List<Country> countries() {
        return countryService.allCountry();
    }
}
