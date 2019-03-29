package ru.iteranet.repo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteranet.entity.Country;
import ru.iteranet.exceptions.RecordAlreadyExistsException;
import ru.iteranet.exceptions.RecordNotFoundException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@DataJpaTest
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
                .findById((long)id)
                .orElseThrow(() -> new RecordNotFoundException(id));

        assertThat(country.getName(), equalTo("Россия"));

    }

    @Test
    public void testCreateCountry() {

        List<Country> countries = repository.findAll();
        List<Country> emptyList = repository.findByName("Абхазия");
        assertThat(countries.size(), equalTo(3));
        assertThat(emptyList.size(), equalTo(0));

        Country country = new Country("Абхазия");
        List<Country> existingCountry = repository.findByName(country.getName());
        if (existingCountry.size() != 0) {
            throw new RecordAlreadyExistsException(country.getName());
        }
        repository.save(country);

        List<Country> countriesAfterAdding = repository.findAll();
        assertThat(countriesAfterAdding.size(), equalTo(4));

    }

    @Test
    public void testDeleteCountry() {

        List<Country> countries = repository.findAll();
        assertThat(countries.size(), equalTo(3));

        repository.delete(repository.findAll().get(2));

        List<Country> countriesAfterDeletion = repository.findAll();
        assertThat(countriesAfterDeletion.size(), equalTo(2));

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

        List<Country> countriesRussia = repository.findByName("Россия");

        assertThat(countriesRussia.size(), equalTo(1));
        assertThat(countriesRussia.get(0).getName(), equalTo("Россия"));
    }
}
