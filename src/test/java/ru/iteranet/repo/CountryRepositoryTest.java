package ru.iteranet.repo;

import org.hamcrest.core.IsNot;
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
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository countryRepository;
    private String countryToCreate = "Абхазия_fsdfsdfsdfsd";
    private String countryToDelete = "Грузия_ыаываывпв";
    private String countryNameToUpdate = "Страна 1";
    private String countryNameToFind = "Россия";


    @Test
    public void testFindAllCountries() {

        List<Country> countries = countryRepository.findAll();
        assertThat(countries.size(), equalTo(3));
        assertThat(countries.get(0).getName(), equalTo("Россия"));
        assertThat(countries.get(1).getName(), equalTo("Бразилия"));
        assertThat(countries.get(2).getName(), equalTo("Франция"));
    }


    @Test
    public void testFindCountryByID() {
        long id = 1;
        Country country = countryRepository.findById(id).get();

        assertThat(country.getId(), equalTo(id));

    }

    @Test
    public void testCreateCountry() {

        // Check that this country not exists and get amount
        List<Country> countries = countryRepository.findAll();
        Country nullCountry = countryRepository.findByName(countryToCreate);
        assertThat(countries, hasSize(3));
        assertThat(nullCountry, nullValue());

        // Add country
        Country country = new Country(countryToCreate);
        Country createdCountry = countryRepository.save(country);
        assertThat(createdCountry, equalTo(country));

        // Check new amount
        List<Country> countriesAfterAdding = countryRepository.findAll();
        assertThat(countriesAfterAdding, hasSize(4));

        // Delete created country
        countryRepository.delete(createdCountry);
    }

    @Test//(expected = RecordNotFoundException.class)
    public void testDeleteCountry() {

        // Create country to delete
        Country country = new Country(countryToDelete);
        Country createdCountry = countryRepository.save(country);
        assertThat(createdCountry, equalTo(country));

        // Delete
        countryRepository.delete(createdCountry);

        // Check that no longer exists
        Country deletedCountry = countryRepository.findById(createdCountry.getId()).isPresent() ?
                countryRepository.findById(createdCountry.getId()).get() : null;
        assertThat(deletedCountry, nullValue());
    }

    @Test
    public void testUpdateCountry() {

        // Get initial size
        List<Country> countries = countryRepository.findAll();
        assertThat(countries, hasSize(3));

        // Update
        Country country = countryRepository.findAll().get(0);
        assertThat(country.getName(), IsNot.not(countryNameToUpdate));
        country.setName(countryNameToUpdate);

        // Check that amount not changed but name changed
        assertThat(countries, hasSize(3));

        assertThat(countries.get(0).getName(), equalTo(countryNameToUpdate));

    }

    @Test
    public void testFindCountryByName() {

        Country russiaCountry = countryRepository.findByName(countryNameToFind);

        assertThat(russiaCountry, equalTo(russiaCountry));
    }
}
