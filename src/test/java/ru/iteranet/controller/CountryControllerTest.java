package ru.iteranet.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CountryControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    private String serverAddress = "http://localhost:8080";
    private String countryToDelete = "Грузия_ыаываывпв";
    private String countryToCreate = "Абхазия_fsdfsdfsdfsd";
    private String countryCantCreateTwoTimes = "Абхазия_fsdfsdfsdfsd";
    private long nonExistingID = 0;
    private long existingID = 1;
    private String emptyCountryName = "";

    @Test
    public void testReadCountries(){

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country", String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Type listType = new TypeToken<ArrayList<Country>>() {
        }.getType();
        List<Country> countryList = new Gson().fromJson(response.getBody(), listType);

        assertThat(countryList, hasSize(4));
        assertThat(countryList.get(0).getName(), equalTo("Россия"));
        assertThat(countryList.get(1).getName(), equalTo("Бразилия"));
        assertThat(countryList.get(2).getName(), equalTo("Франция"));
        assertThat(countryList.get(3).getName(), equalTo(countryToCreate));
    }

    @Test
    public void testFindCountryByID() {

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/"+ existingID, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(response.getBody(), type);

        assertThat(createdCountry.getName(), equalTo("Россия"));
    }

    @Test
    public void testCantFindNonExistingCountryByID() {

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/"+ nonExistingID, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }
    @Test
    public void testCreateCountry(){
        String findByNameURL = serverAddress +"/api/country?name=";

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

        assertThat(nullCountry, equalTo(null));

        // Create new country
        Country country = new Country(countryToCreate);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(responseAfterCreation.getBody(), type);

        assertThat(createdCountry.getName(), notNullValue());
        assertThat(createdCountry.getName(), equalTo(countryToCreate));
    }

    @Test
    public void testCantCreateEmptyCountry(){
        String url = serverAddress +"/api/country?name=";

        // Create new country
        Country country = new Country(emptyCountryName);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testCantCreateCountryTwoTimes(){

        // Create country
        Country country = new Country(countryCantCreateTwoTimes);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(responseAfterCreation.getBody(), type);

        assertThat(createdCountry.getName(), notNullValue());
        assertThat(createdCountry.getName(), equalTo(countryCantCreateTwoTimes));

        // Check that cant create again
        ResponseEntity<String> responseAfterCreation2 = testRestTemplate.postForEntity("/api/country", country, String.class);
        assertThat(responseAfterCreation2.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

        // Delete
        ResponseEntity<Country> responseDelete = testRestTemplate.exchange("/api/country/"+createdCountry.getId(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Country.class);

        assertThat(responseDelete.getStatusCode(), equalTo(HttpStatus.OK));

        // Check that no longer exists
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/"+ createdCountry.getId(), String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testDeleteCountry() {

        // Create country to delete
        Country country = new Country(countryToDelete);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(responseAfterCreation.getBody(), type);

        assertThat(createdCountry.getName(), notNullValue());
        assertThat(createdCountry.getName(), equalTo(countryToDelete));


        // Delete
        ResponseEntity<Country> responseDelete = testRestTemplate.exchange("/api/country/"+createdCountry.getId(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Country.class);

        assertThat(responseDelete.getStatusCode(), equalTo(HttpStatus.OK));

        // Check that no longer exists
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/"+ createdCountry.getId(), String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testCantDeleteNonExistingCountry() {

        // Check that country doesn't exists
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/"+ nonExistingID, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

        // Delete country
        ResponseEntity<Country> responseDelete = testRestTemplate.exchange("/api/country/"+ nonExistingID,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Country.class);

        assertThat(responseDelete.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testUpdateCountry() {

        String newName = "Страна1";
        String url = serverAddress + "/api/country/"+existingID;

        // Check that country exists
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        // Update country
        Country country = new Country(newName);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Country> requestUpdate = new HttpEntity<>(country, headers);

        ResponseEntity<String> responsePut = testRestTemplate.exchange(url, HttpMethod.PUT, requestUpdate, String.class);

        assertThat(responsePut.getStatusCode(), equalTo(HttpStatus.OK));

        // Check new name
        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(responsePut.getBody(), type);

        assertThat(createdCountry.getName(), equalTo(newName));

    }


    @Test
    public void testCantUpdateNonExistingCountry() {

        String newName = "Страна1";
        String url = serverAddress + "/api/country/"+nonExistingID;

        // Check that country doesn't exist
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

        // Update country
        Country country = new Country(newName);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Country> requestUpdate = new HttpEntity<>(country, headers);

        ResponseEntity<String> responsePut = testRestTemplate.exchange(url, HttpMethod.PUT, requestUpdate, String.class);

        assertThat(responsePut.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }


    @Test
    public void testFindCountryByName() {
        String url = serverAddress + "/api/country?name=";
        String countryToFind = "Россия";

        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = testRestTemplate.exchange(
                url + countryToFind,
                HttpMethod.GET,
                entity,
                String.class);

        Type listType = new TypeToken<Country>() {
        }.getType();
        Country foundCountry = new Gson().fromJson(response.getBody(), listType);

        assertThat(foundCountry, notNullValue());
        assertThat(foundCountry.getName(), equalTo(countryToFind));
    }
}
