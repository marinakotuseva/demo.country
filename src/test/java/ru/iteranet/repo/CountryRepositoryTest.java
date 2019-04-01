package ru.iteranet.repo;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteranet.entity.Country;
import ru.iteranet.exceptions.RecordNotFoundException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringRunner.class)
@DataJpaTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository countryRepository;
    private String countryToCreate = "Абхазия_fsdfsdfsdfsd";
    private String countryToDelete = "Грузия_ыаываывпв";


    @Test
    public void testReadCountries() {

        List<Country> countries = countryRepository.findAll();
        assertThat(countries.size(), equalTo(3));
        assertThat(countries.get(0).getName(), equalTo("Россия"));
        assertThat(countries.get(1).getName(), equalTo("Бразилия"));
        assertThat(countries.get(2).getName(), equalTo("Франция"));
    }


    @Test
    public void testFindCountryByID() {
        long id = 1;
        Country country = countryRepository
                .findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));

        assertThat(country.getName(), equalTo("Россия"));

    }

    @Test
    public void testCreateCountry() {

        // Check that this country not exists
        List<Country> countries = countryRepository.findAll();
        Country nullCountry = countryRepository.findByName(countryToCreate);
        assertThat(countries.size(), equalTo(3));
        assertThat(nullCountry, equalTo(null));

        // Add country
        Country country = new Country(countryToCreate);
        countryRepository.save(country);

        // Check that now exists
        List<Country> countriesAfterAdding = countryRepository.findAll();
        Country createdCountry = countryRepository.findByName(countryToCreate);
        assertThat(countriesAfterAdding.size(), equalTo(4));
        assertThat(createdCountry, notNullValue());
    }

    @Test(expected = RecordNotFoundException.class)
    public void testDeleteCountry() {

        // Create country to delete
        Country country = new Country(countryToDelete);
        countryRepository.save(country);
        Country createdCountry = countryRepository.findByName(countryToDelete);
        assertThat(createdCountry, notNullValue());

        // Delete
        countryRepository.delete(createdCountry);

        // Check that no longer exists
        Country deletedCountry = countryRepository
                .findById(createdCountry.getId())
                .orElseThrow(() -> new RecordNotFoundException(createdCountry.getId()));
    }

    @Test
    public void testUpdateCountry() {

        // Get initial size
        List<Country> countries = countryRepository.findAll();
        assertThat(countries.size(), equalTo(3));

        // Update
        Country country = countryRepository.findAll().get(0);
        country.setName("Страна 1");

        // Check that amount not changed but name changed
        List<Country> countriesAfterEditing = countryRepository.findAll();
        assertThat(countriesAfterEditing.size(), equalTo(3));

        assertThat(countriesAfterEditing.get(0).getName(), equalTo("Страна 1"));

    }

    @Test
    public void testFindCountryByName() {

        Country russiaCountry = countryRepository.findByName("Россия");

        assertThat(russiaCountry, notNullValue());
        assertThat(russiaCountry.getName(), equalTo("Россия"));
    }
}
