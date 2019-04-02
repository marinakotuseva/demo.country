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
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CountryControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    private String serverAddress = "http://localhost:8080";
    private String countryToDelete = "Страна_для_удаления";
    private String countryToCreate = "Страна_для_создания";
    private String countryCantCreateTwoTimes = "Страна_повторное_создание";
    private String countryNameToChangeTo = "Измененное_имя";
    private String countryEmptyName = "";
    private String countryExistingNameToFind = "Россия";

    private long nonExistingID = 0;
    private long existingID = 1;

    @Test
    public void testFindAllCountries() {

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country", String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Type listType = new TypeToken<ArrayList<Country>>() {
        }.getType();
        List<Country> countryList = new Gson().fromJson(response.getBody(), listType);

        assertThat(countryList, hasSize(4));
        assertThat(countryList.get(0).getName(), equalTo("Россия"));
        assertThat(countryList.get(1).getName(), equalTo("Бразилия"));
        assertThat(countryList.get(2).getName(), equalTo("Франция"));
    }

    @Test
    public void testFindCountryByID() {

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/" + existingID, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(response.getBody(), type);

        assertThat(createdCountry, notNullValue());
        assertThat(createdCountry.getName(), equalTo(countryExistingNameToFind));
    }

    @Test
    public void testCantFindNonExistingCountryByID() {

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/" + nonExistingID, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testCreateCountry() {
        String findByNameURL = serverAddress + "/api/country?name=";

        // Check that this country do not exists
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = testRestTemplate.exchange(
                findByNameURL + countryToCreate,
                HttpMethod.GET,
                entity,
                String.class);

        Type listType = new TypeToken<Country>() {
        }.getType();
        Country nullCountry = new Gson().fromJson(response.getBody(), listType);

        assertThat(nullCountry, nullValue());

        // Create new country
        Country country = new Country(countryToCreate);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(responseAfterCreation.getBody(), type);

        assertThat(createdCountry, equalTo(country));
    }

    @Test
    public void testCantCreateEmptyCountry() {
        String url = serverAddress + "/api/country?name=";

        // Create new country
        Country country = new Country(countryEmptyName);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testCantCreateCountryTwoTimes() {

        // Create country
        Country country = new Country(countryCantCreateTwoTimes);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(responseAfterCreation.getBody(), type);

        assertThat(createdCountry, equalTo(country));

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

        // Create country to delete
        Country country = new Country(countryToDelete);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);
        System.out.println(country.getId());
        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(responseAfterCreation.getBody(), type);
        System.out.println(createdCountry.getId());
        assertThat(createdCountry, equalTo(country));


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

        // Check that country doesn't exists
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/" + nonExistingID, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

        // Delete country
        ResponseEntity<Country> responseDelete = testRestTemplate.exchange("/api/country/" + nonExistingID,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Country.class);

        assertThat(responseDelete.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testUpdateCountry() {

        String url = serverAddress + "/api/country/" + existingID;

        // Check that country exists
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        // Update country
        Country country = new Country(countryNameToChangeTo);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Country> requestUpdate = new HttpEntity<>(country, headers);

        ResponseEntity<String> responsePut = testRestTemplate.exchange(url, HttpMethod.PUT, requestUpdate, String.class);

        assertThat(responsePut.getStatusCode(), equalTo(HttpStatus.OK));

        // Check new name
        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(responsePut.getBody(), type);

        assertThat(createdCountry.getName(), equalTo(country.getName()));

    }


    @Test
    public void testCantUpdateNonExistingCountry() {

        String url = serverAddress + "/api/country/" + nonExistingID;

        // Check that country doesn't exist
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

        // Update country
        Country country = new Country(countryNameToChangeTo);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Country> requestUpdate = new HttpEntity<>(country, headers);

        ResponseEntity<String> responsePut = testRestTemplate.exchange(url, HttpMethod.PUT, requestUpdate, String.class);

        assertThat(responsePut.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }


    @Test
    public void testFindCountryByName() {
        String url = serverAddress + "/api/country?name=";

        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = testRestTemplate.exchange(
                url + countryExistingNameToFind,
                HttpMethod.GET,
                entity,
                String.class);

        Type listType = new TypeToken<Country>() {
        }.getType();
        Country foundCountry = new Gson().fromJson(response.getBody(), listType);

        assertThat(foundCountry, notNullValue());
        assertThat(foundCountry.getName(), equalTo(countryExistingNameToFind));
    }
}
