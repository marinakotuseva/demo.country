package ru.iteranet.repo;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteranet.entity.Country;
import ru.iteranet.exceptions.RecordAlreadyExistsException;
import ru.iteranet.exceptions.RecordNotFoundException;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringRunner.class)
@DataJpaTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository repository;


    @Test
    public void testReadCountries() {

        List<Country> countries = repository.findAll();
        assertThat(countries.size(), equalTo(3));
        assertThat(countries.get(0).getName(), equalTo("Россия"));
        assertThat(countries.get(1).getName(), equalTo("Бразилия"));
        assertThat(countries.get(2).getName(), equalTo("Франция"));
    }


    @Test
    public void testFindCountryByID() {
        long id = 1;
        Country country = repository
                .findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));

        assertThat(country.getName(), equalTo("Россия"));

    }

    @Test
    public void testCreateCountry() {
        String countryToCreate = "Абхазия_fsdfsdfsdfsd";

        List<Country> countries = repository.findAll();
        Country nullCountry = repository.findByName(countryToCreate);
        assertThat(countries.size(), equalTo(3));
        assertThat(nullCountry, equalTo(null));

        Country country = new Country(countryToCreate);
        repository.save(country);

        List<Country> countriesAfterAdding = repository.findAll();
        Country createdCountry = repository.findByName(countryToCreate);
        assertThat(countriesAfterAdding.size(), equalTo(4));
        assertThat(createdCountry, notNullValue());
    }

    @Test(expected = RecordNotFoundException.class)
    public void testDeleteCountry() {

        String countryToDelete = "Грузия_ыаываывпв";

        // Create country to delete
        Country country = new Country(countryToDelete);
        repository.save(country);
        Country createdCountry = repository.findByName(countryToDelete);
        assertThat(createdCountry, notNullValue());

        // Delete
        repository.delete(createdCountry);

        // Check that no longer exists
        Country deletedCountry = repository
                .findById(createdCountry.getId())
                .orElseThrow(() -> new RecordNotFoundException(createdCountry.getId()));
    }

    @Test
    public void testUpdateCountry() {

        List<Country> countries = repository.findAll();
        assertThat(countries.size(), equalTo(3));

        Country country = repository.findAll().get(0);
        country.setName("Страна 1");

        List<Country> countriesAfterEditing = repository.findAll();
        assertThat(countriesAfterEditing.size(), equalTo(3));

        assertThat(countriesAfterEditing.get(0).getName(), equalTo("Страна 1"));

    }

    @Test
    public void testFindCountryByName() {

        Country russiaCountry = repository.findByName("Россия");

        assertThat(russiaCountry, notNullValue());
        assertThat(russiaCountry.getName(), equalTo("Россия"));
    }
}
