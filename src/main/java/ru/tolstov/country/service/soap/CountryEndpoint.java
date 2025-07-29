package ru.tolstov.country.service.soap;

import guru.qa.xml.country.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.tolstov.country.config.SoapConfig;
import ru.tolstov.country.domain.Country;
import ru.tolstov.country.domain.CountryInput;
import ru.tolstov.country.domain.CountryUpdateInput;
import ru.tolstov.country.service.CountryService;

import java.util.List;
import java.util.UUID;

@Endpoint
public class CountryEndpoint {
    private final CountryService countryService;

    @Autowired
    public CountryEndpoint(CountryService countryService) {
        this.countryService = countryService;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "idRequest")
    @ResponsePayload
    public CountryResponse country(@RequestPayload IdRequest request) {
        Country country = countryService.getCountry(UUID.fromString(request.getId()));
        guru.qa.xml.country.Country xmlCountry = new guru.qa.xml.country.Country();
        xmlCountry.setId(country.id().toString());
        xmlCountry.setCode(country.code());
        xmlCountry.setName(country.name());
        CountryResponse response = new CountryResponse();
        response.setCountry(xmlCountry);
        return response;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "addCountryRequest")
    @ResponsePayload
    public CountryResponse addCountry(@RequestPayload AddCountryRequest request) {
        Country country = countryService.addCountry(new CountryInput(request.getCountry().getName(), request.getCountry().getCode()));
        guru.qa.xml.country.Country xmlCountry = new guru.qa.xml.country.Country();
        xmlCountry.setId(country.id().toString());
        xmlCountry.setCode(country.code());
        xmlCountry.setName(country.name());
        CountryResponse response = new CountryResponse();
        response.setCountry(xmlCountry);
        return response;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "updateCountryRequest")
    @ResponsePayload
    public CountryResponse updateCountry(@RequestPayload UpdateCountryRequest request) {
        Country country = countryService.updateCountry(
                new CountryUpdateInput(
                        UUID.fromString(request.getCountry().getId()),
                        request.getCountry().getName(),
                        request.getCountry().getCode()
                )
        );
        guru.qa.xml.country.Country xmlCountry = new guru.qa.xml.country.Country();
        xmlCountry.setId(country.id().toString());
        xmlCountry.setCode(country.code());
        xmlCountry.setName(country.name());
        CountryResponse response = new CountryResponse();
        response.setCountry(xmlCountry);
        return response;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "allCountryRequest")
    @ResponsePayload
    public CountriesResponse allCountries(@RequestPayload AllCountryRequest request) {
        List<Country> countryList = countryService.allCountriesGql();
        CountriesResponse response = new CountriesResponse();
        response.getCountry().addAll(
                countryList.stream().map(country ->
                        {
                            guru.qa.xml.country.Country xmlCountry = new guru.qa.xml.country.Country();
                            xmlCountry.setId(country.id().toString());
                            xmlCountry.setCode(country.code());
                            xmlCountry.setName(country.name());
                            return xmlCountry;
                        }
                ).toList()
        );
        return response;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "allCountryPageRequest")
    @ResponsePayload
    public CountriesResponse countries(@RequestPayload AllCountryPageRequest request) {
        Page<Country> countries = countryService.allCountry(PageRequest.of(
                        request.getPageInfo().getPage(),
                        request.getPageInfo().getSize()
                )
        );

        CountriesResponse response = new CountriesResponse();
        response.getCountry().addAll(
                countries.stream().map(country ->
                        {
                            guru.qa.xml.country.Country xmlCountry = new guru.qa.xml.country.Country();
                            xmlCountry.setId(country.id().toString());
                            xmlCountry.setCode(country.code());
                            xmlCountry.setName(country.name());
                            return xmlCountry;
                        }
                ).toList()
        );
        return response;
    }

}
