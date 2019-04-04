package ru.iteranet.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteranet.entity.Country;
import ru.iteranet.exceptions.NotSupportedClassExceptions;
import ru.iteranet.utils.CommonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CountryControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String serverAddress = "http://localhost:8080";


    @Test
    public void testFindAllCountries() {

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country", String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Type listType = new TypeToken<ArrayList<Country>>() {
        }.getType();
        List<Country> countryList = new Gson().fromJson(response.getBody(), listType);

        assertThat(countryList, hasSize(3));
        assertThat(countryList.get(0).getName(), equalTo("Россия"));
        assertThat(countryList.get(1).getName(), equalTo("Бразилия"));
        assertThat(countryList.get(2).getName(), equalTo("Франция"));
    }

    @Test
    public void testFindCountryById() throws NotSupportedClassExceptions {

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/1", String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Country foundCountry = CommonUtils.responseToObject(Country.class, response);
        assertThat(foundCountry.getName(), equalTo("Россия"));
    }

    @Test
    public void testCantFindNonExistingCountryById() {

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/0", String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testCreateCountry() throws NotSupportedClassExceptions {

        String countryName = "Страна_для_создания";

        // Check that this country do not exists
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = testRestTemplate.exchange(
                serverAddress + "/api/country?name=" + countryName,
                HttpMethod.GET,
                entity,
                String.class);
        assertThat(response.getBody(), nullValue());

        // Create new country
        Country country = new Country(countryName);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);
        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.OK));

        Country createdCountry = CommonUtils.responseToObject(Country.class, responseAfterCreation);
        assertThat(createdCountry.getName(), equalTo(country.getName()));

        // Delete created country
        ResponseEntity<Country> responseDelete = testRestTemplate.exchange("/api/country/" + createdCountry.getId(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Country.class);

        assertThat(responseDelete.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void testCantCreateEmptyCountry() {

        // Create new country
        Country country = new Country("");
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testCantCreateCountryTwoTimes() throws NotSupportedClassExceptions {

        // Create country
        Country country = new Country("Страна_повторное_создание");
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.OK));
        Country createdCountry = CommonUtils.responseToObject(Country.class, responseAfterCreation);

        assertThat(createdCountry.getName(), equalTo(country.getName()));

        // Check that cant create again
        ResponseEntity<String> responseAfterCreation2 = testRestTemplate.postForEntity("/api/country", country, String.class);
        assertThat(responseAfterCreation2.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

        // Delete
        ResponseEntity<Country> responseDelete = testRestTemplate.exchange("/api/country/" + createdCountry.getId(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Country.class);

        assertThat(responseDelete.getStatusCode(), equalTo(HttpStatus.OK));

        // Check that no longer exists
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/" + createdCountry.getId(), String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testDeleteCountry() throws NotSupportedClassExceptions {

        // Create country to delete
        Country country = new Country("Страна_для_удаления");
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);
        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.OK));

        Country createdCountry = CommonUtils.responseToObject(Country.class, responseAfterCreation);
        assertThat(createdCountry.getName(), equalTo(country.getName()));


        // Delete
        ResponseEntity<Country> responseDelete = testRestTemplate.exchange("/api/country/" + createdCountry.getId(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Country.class);

        assertThat(responseDelete.getStatusCode(), equalTo(HttpStatus.OK));

        // Check that no longer exists
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/" + createdCountry.getId(), String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testCantDeleteNonExistingCountry() {
        long id = 0;

        // Check that country doesn't exists
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/" + id, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

        // Delete country
        ResponseEntity<Country> responseDelete = testRestTemplate.exchange("/api/country/" + id,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Country.class);

        assertThat(responseDelete.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testUpdateCountry() throws NotSupportedClassExceptions {

        long id = 1;

        String url = serverAddress + "/api/country/" + id;
        String countryName = "Измененное_имя";

        // Check that country exists
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        Country existingCountry = CommonUtils.responseToObject(Country.class, response);
        assertThat(existingCountry.getName(), not(equalTo(countryName)));

        // Update found country
        existingCountry.setName(countryName);

        ResponseEntity<String> responseAfterEditing = testRestTemplate.postForEntity("/api/country/"+id, existingCountry, String.class);

        assertThat(responseAfterEditing.getStatusCode(), equalTo(HttpStatus.OK));

        Country editedCountry = CommonUtils.responseToObject(Country.class, responseAfterEditing);
        assertThat(editedCountry.getName(), equalTo(existingCountry.getName()));

        // Change back
        ResponseEntity<String> responseAfterEditing2 = testRestTemplate.postForEntity("/api/country/"+id, existingCountry, String.class);

        assertThat(responseAfterEditing2.getStatusCode(), equalTo(HttpStatus.OK));

    }


    @Test
    public void testCantUpdateNonExistingCountry() {

        long id = 0;
        String url = serverAddress + "/api/country/" + id;

        // Check that country doesn't exist
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

        // Update country
        Country country = new Country("Россия");
        ResponseEntity<String> responseAfterEditing = testRestTemplate.postForEntity("/api/country/"+id, country, String.class);

        assertThat(responseAfterEditing.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
    }


    @Test
    public void testFindCountryByName() throws NotSupportedClassExceptions {

        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = testRestTemplate.exchange(
                serverAddress + "/api/country?name=" + "Россия",
                HttpMethod.GET,
                entity,
                String.class);

        Country foundCountry = CommonUtils.responseToObject(Country.class, response);

        assertThat(foundCountry.getId(), equalTo((long)1));
    }
}
