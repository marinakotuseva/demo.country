package ru.iteranet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteranet.Entity.Country;
import ru.iteranet.Repo.CountryRepository;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository repository;


    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void can_read_countries() {

        List<Country> countries = repository.findAll();
        assertEquals(3, countries.size());
        assertEquals("Россия", countries.get(0).getName());
        assertEquals("Бразилия", countries.get(1).getName());
        assertEquals("Франция", countries.get(2).getName());

    }

    @Test
    public void can_create_country() {

        Country country = new Country("Голландия");
        repository.save(country);

        List<Country> countries = repository.findAll();
        assertEquals(4, countries.size());
        assertEquals("Россия", countries.get(0).getName());
        assertEquals("Бразилия", countries.get(1).getName());
        assertEquals("Франция", countries.get(2).getName());
        assertEquals("Голландия", countries.get(3).getName());

    }

    @Test
    public void can_delete_country() {

        repository.delete(repository.findAll().get(2));

        List<Country> countries = repository.findAll();
        assertEquals(2, countries.size());
        assertEquals("Россия", countries.get(0).getName());
        assertEquals("Бразилия", countries.get(1).getName());

    }

    @Test
    public void can_update_country() {
        Country country = repository.findAll().get(0);
        country.setName("Страна 1");

        List<Country> countries = repository.findAll();
        assertEquals(3, countries.size());
        assertEquals("Страна 1", countries.get(0).getName());
        assertEquals("Бразилия", countries.get(1).getName());
        assertEquals("Франция", countries.get(2).getName());

    }

    @Test
    public void can_find_country_by_name() {

        List<Country> countriesRussia = repository.findByName("Россия");

        assertEquals(1, countriesRussia.size());
        assertEquals("Россия", countriesRussia.get(0).getName());
    }
}
