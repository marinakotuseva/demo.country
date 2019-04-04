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
        assertThat(allCountries.size(), equalTo(3));
        assertThat(allCountries.get(0).getName(), equalTo("Россия"));
        assertThat(allCountries.get(1).getName(), equalTo("Бразилия"));
        assertThat(allCountries.get(2).getName(), equalTo("Франция"));

        // Find country by ID
        Country country = countryRepository.findById((long)1).get();
        assertThat(country.getName(), equalTo("Россия"));

        // Find country by name
        Country russiaCountry = countryRepository.findByName("Россия");
        assertThat(russiaCountry.getId(), equalTo((long)1));

        // Create new country
        // Check that not exists
        assertThat(allCountries.contains(countryName), equalTo(false));
        // Add country
        Country createdCountry = countryRepository.save(new Country(countryName));
        assertThat(createdCountry.getName(), equalTo(countryName));
        // Check new amount
        List<Country> countriesAfterAdding = countryRepository.findAll();
        assertThat(countriesAfterAdding, hasSize(4));


        // Update added country
        createdCountry.setName(newCountryName);
        countryRepository.save(createdCountry);

        // Check that amount not changed but name changed
        List<Country> countriesAfterUpdate = countryRepository.findAll();
        assertThat(countriesAfterUpdate, hasSize(4));
        assertThat(countriesAfterUpdate.get(3).getName(), equalTo(newCountryName));

        // Delete updated country
        countryRepository.delete(createdCountry);

        // Check that no longer exists
        assertThat(countryRepository.findAll().contains(createdCountry.getName()), equalTo(false));
    }
}
