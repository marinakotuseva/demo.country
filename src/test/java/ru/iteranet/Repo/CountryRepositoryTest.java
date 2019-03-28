package ru.iteranet.Repo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteranet.Entity.Country;
import ru.iteranet.Exceptions.RecordAlreadyExistsException;
import ru.iteranet.Exceptions.RecordNotFoundException;
import ru.iteranet.Repo.CountryRepository;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository repository;


    @Test
    public void testReadCountries() {

        List<Country> countries = repository.findAll();
        assertEquals(3, countries.size());
        assertEquals("Россия", countries.get(0).getName());
        assertEquals("Бразилия", countries.get(1).getName());
        assertEquals("Франция", countries.get(2).getName());

    }


    @Test
    public void testFindCountryByID() {
        long id = 1;
        Country country = repository
                .findById((long)id)
                .orElseThrow(() -> new RecordNotFoundException(id));

        assertEquals("Россия", country.getName());

    }

    @Test
    public void testCreateCountry() {

        List<Country> countries = repository.findAll();
        assertEquals(3, countries.size());

        Country country = new Country("Абхазия");
        List<Country> existingCountry = repository.findByName(country.getName());
        if (existingCountry.size() != 0) {
            throw new RecordAlreadyExistsException(country.getName());
        }
        repository.save(country);

        List<Country> countriesAfterAdding = repository.findAll();
        assertEquals(4, countriesAfterAdding.size());

    }

    @Test
    public void testDeleteCountry() {

        List<Country> countries = repository.findAll();
        assertEquals(3, countries.size());

        repository.delete(repository.findAll().get(2));

        List<Country> countriesAfterDeletion = repository.findAll();
        assertEquals(2, countriesAfterDeletion.size());

    }

    @Test
    public void testUpdateCountry() {

        List<Country> countries = repository.findAll();
        assertEquals(3, countries.size());

        Country country = repository.findAll().get(0);
        country.setName("Страна 1");

        List<Country> countriesAfterEditing = repository.findAll();
        assertEquals(3, countriesAfterEditing.size());

        assertEquals("Страна 1", countriesAfterEditing.get(0).getName());

    }

    @Test
    public void testFindCountryByName() {

        List<Country> countriesRussia = repository.findByName("Россия");

        assertEquals(1, countriesRussia.size());
        assertEquals("Россия", countriesRussia.get(0).getName());
    }
}
