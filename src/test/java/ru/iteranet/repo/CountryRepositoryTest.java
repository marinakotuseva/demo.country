package ru.iteranet.repo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteranet.entity.Country;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository countryRepository;


    @Test
    public void testCountryRepository(){
        String countryName = "Страна 1";
        String newCountryName = "Страна 1.1";

        // Read all countries
        List<Country> allCountries = countryRepository.findAll();
        assertThat(allCountries.size(), greaterThanOrEqualTo(3));
        assertThat(allCountries.get(0).getName(), equalTo("Россия"));
        assertThat(allCountries.get(1).getName(), equalTo("Бразилия"));
        assertThat(allCountries.get(2).getName(), equalTo("Франция"));
        int initialCountrySize = allCountries.size();

        // Find country by ID
        Country country = countryRepository.findById(1L).get();
        assertThat(country.getName(), equalTo("Россия"));

        // Find country by name
        Country russiaCountry = countryRepository.findByName("Россия");
        assertThat(russiaCountry.getId(), equalTo(1L));

        // Create new country
        Country newCountry = new Country(countryName);
        // Check that not exists
        assertThat(allCountries.contains(newCountry), equalTo(false));
        // Add country
        Country createdCountry = countryRepository.save(newCountry);
        assertThat(createdCountry.getName(), equalTo(newCountry.getName()));
        // Check new amount
        List<Country> countriesAfterAdding = countryRepository.findAll();
        assertThat(countriesAfterAdding, hasSize(initialCountrySize+1));
        assertThat(countriesAfterAdding.contains(createdCountry), equalTo(true));

        // Update added country
        createdCountry.setName(newCountryName);
        Country countryAfterUpdate = countryRepository.save(createdCountry);

        // Check that amount not changed but name changed
        List<Country> countriesAfterUpdate = countryRepository.findAll();
        assertThat(countriesAfterUpdate, hasSize(initialCountrySize+1));
        assertThat(countriesAfterUpdate.contains(countryAfterUpdate), equalTo(true));

        // Delete updated country
        countryRepository.delete(createdCountry);

        // Check that no longer exists
        assertThat(countryRepository.findAll().contains(countryAfterUpdate), equalTo(false));
    }
}
