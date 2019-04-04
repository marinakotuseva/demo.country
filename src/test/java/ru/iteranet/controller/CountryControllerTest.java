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
    public void testFindCountryByID() {
        String countryName = "Россия";
        long id = 1;

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/" + id, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country foundCountry = new Gson().fromJson(response.getBody(), type);

        assertThat(foundCountry.getName(), equalTo(countryName));
    }

    @Test
    public void testCantFindNonExistingCountryByID() {
        long id = 0;

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/" + id, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testCreateCountry() {
        String countryName = "Страна_для_создания";

        // Check that this country do not exists
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = testRestTemplate.exchange(
                serverAddress + "/api/country?name=" + countryName,
                HttpMethod.GET,
                entity,
                String.class);

        Type listType = new TypeToken<Country>() {
        }.getType();
        Country nonExistingCountry = new Gson().fromJson(response.getBody(), listType);

        assertThat(nonExistingCountry, nullValue());

        // Create new country
        Country country = new Country(countryName);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(responseAfterCreation.getBody(), type);
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
        String countryName = "";

        // Create new country
        Country country = new Country(countryName);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testCantCreateCountryTwoTimes() {
        String countryName = "Страна_повторное_создание";

        // Create country
        Country country = new Country(countryName);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(responseAfterCreation.getBody(), type);
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
    public void testDeleteCountry() {
        String countryName = "Страна_для_удаления";

        // Create country to delete
        Country country = new Country(countryName);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);
        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(responseAfterCreation.getBody(), type);
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
    public void testUpdateCountry() {
        long id = 1;

        String url = serverAddress + "/api/country/" + id;
        String countryName = "Измененное_имя";

        // Check that country exists
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        Type type = new TypeToken<Country>() {
        }.getType();
        Country foundCountry = new Gson().fromJson(response.getBody(), type);
        String oldName = foundCountry.getName();
        assertThat(oldName, not(equalTo(countryName)));

        // Update found country
        foundCountry.setName(countryName);

        ResponseEntity<String> responseAfterEditing = testRestTemplate.postForEntity("/api/country/"+id, foundCountry, String.class);

        assertThat(responseAfterEditing.getStatusCode(), equalTo(HttpStatus.OK));

        Type type2 = new TypeToken<Country>() {
        }.getType();
        Country editedCountry = new Gson().fromJson(responseAfterEditing.getBody(), type2);
        assertThat(editedCountry.getName(), equalTo(foundCountry.getName()));

        // Change back
        ResponseEntity<String> responseAfterEditing2 = testRestTemplate.postForEntity("/api/country/"+id, foundCountry, String.class);

        assertThat(responseAfterEditing2.getStatusCode(), equalTo(HttpStatus.OK));

    }


    @Test
    public void testCantUpdateNonExistingCountry() {
        String countryName = "Россия";
        long id = 0;
        String url = serverAddress + "/api/country/" + id;

        // Check that country doesn't exist
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

        // Update country
        Country country = new Country(countryName);
        ResponseEntity<String> responseAfterEditing = testRestTemplate.postForEntity("/api/country/"+id, country, String.class);

        assertThat(responseAfterEditing.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }


    @Test
    public void testFindCountryByName() {
        String countryName = "Россия";

        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = testRestTemplate.exchange(
                serverAddress + "/api/country?name=" + countryName,
                HttpMethod.GET,
                entity,
                String.class);

        Type listType = new TypeToken<Country>() {
        }.getType();
        Country foundCountry = new Gson().fromJson(response.getBody(), listType);

        assertThat(foundCountry, notNullValue());
        assertThat(foundCountry.getName(), equalTo(countryName));
    }
}
