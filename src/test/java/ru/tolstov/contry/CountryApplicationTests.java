package ru.tolstov.contry;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tolstov.contry.domain.Country;
import ru.tolstov.contry.domain.CountryInput;
import ru.tolstov.contry.service.CountryService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CountryApplicationTests {

    @Autowired
    private CountryService dbCountryService;

    @Test
    void testAddCountryWithNullName() {
        CountryInput country = new CountryInput(null, "US");
        assertThrows(IllegalArgumentException.class, () -> dbCountryService.addCountry(country));
    }

    @Test
    void testAddCountryWithNullCode() {
        CountryInput country = new CountryInput("United States", null);
        assertThrows(IllegalArgumentException.class, () -> dbCountryService.addCountry(country));
    }

}
