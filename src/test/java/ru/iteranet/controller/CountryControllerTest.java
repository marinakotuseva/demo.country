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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CountryControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    private String site = "http://localhost:8080";

    @Test
    public void testReadCountries(){

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country", String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Type listType = new TypeToken<ArrayList<Country>>() {
        }.getType();
        List<Country> countryList = new Gson().fromJson(response.getBody(), listType);

        assertThat(countryList, hasSize(4));

        Country country1 = countryList.get(0);
        assertThat(country1.getName(), equalTo("Россия"));
        Country country2 = countryList.get(1);
        assertThat(country2.getName(), equalTo("Бразилия"));
    }

    @Test
    public void testFindCountryByID() {

        long id = 1;

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/"+ id, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(response.getBody(), type);

        assertThat(createdCountry.getName(), equalTo("Россия"));
    }

    @Test
    public void testCantFindNonExistingCountryByID() {

        long id = 10;

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/"+ id, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }
    @Test
    public void testCreateCountry(){
        String url = site+"/api/country?name=";
        String countryName = "Абхазия";

        // Check that this country do not exists
        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = testRestTemplate.exchange(
                url + countryName,
                HttpMethod.GET,
                entity,
                String.class);

        Type listType = new TypeToken<ArrayList<Country>>() {
        }.getType();
        List<Country> countryList = new Gson().fromJson(response.getBody(), listType);

        assertThat(countryList, hasSize(0));

        // Create new country
        Country country = new Country(countryName);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(responseAfterCreation.getBody(), type);

        assertThat(createdCountry.getName(), equalTo(countryName));
    }

    @Test
    public void testCantCreateEmptyCountry(){
        String url = site+"/api/country?name=";
        String countryName = "";

        // Create new country
        Country country = new Country(countryName);
        ResponseEntity<String> responseAfterCreation = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(responseAfterCreation.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testCantCreateCountryTwoTimes(){

        Country country = new Country("Грузия");
        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/country", country, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        ResponseEntity<String> response2 = testRestTemplate.postForEntity("/api/country", country, String.class);

        System.out.println(response2);
        assertThat(response2.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testDeleteCountry() {

        long id = 3;
        int initialAmount;

        // Check that country exists
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/"+ id, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        // Current amount
        ResponseEntity<String> responseGetAll = testRestTemplate.getForEntity("/api/country", String.class);
        Type listType = new TypeToken<ArrayList<Country>>() {
        }.getType();
        List<Country> countryList = new Gson().fromJson(responseGetAll.getBody(), listType);
        initialAmount = countryList.size();


        // Delete country
        ResponseEntity<Country> responseDelete = testRestTemplate.exchange("/api/country/"+id,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Country.class);

        assertThat(responseDelete.getStatusCode(), equalTo(HttpStatus.OK));

        ResponseEntity<String> responseAfterDeletion = testRestTemplate.getForEntity("/api/country", String.class);

        assertThat(responseAfterDeletion.getStatusCode(), equalTo(HttpStatus.OK));

        Type listType2 = new TypeToken<ArrayList<Country>>() {
        }.getType();
        List<Country> countryListAfterDeletion = new Gson().fromJson(responseAfterDeletion.getBody(), listType2);

        assertThat(countryListAfterDeletion, hasSize(initialAmount-1));

    }

    @Test
    public void testCantDeleteNonExistingCountry() {

        long id = 30;

        // Check that country doesn't exists
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country/"+ id, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

        // Delete country
        ResponseEntity<Country> responseDelete = testRestTemplate.exchange("/api/country/"+ id,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Country.class);

        assertThat(responseDelete.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testUpdateCountry() {

        String newName = "Страна1";
        long id = 1;
        String url = site + "/api/country/"+id;

        // Check that country exists
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        // Update country
        Country country = new Country(newName);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Country> requestUpdate = new HttpEntity<>(country, headers);

        ResponseEntity<String> responsePut = testRestTemplate.exchange(url, HttpMethod.PUT, requestUpdate, String.class);

        System.out.println(responsePut);
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
        long id = 10;
        String url = site + "/api/country/"+id;

        // Check that country doesn't exist
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

        // Update country
        Country country = new Country(newName);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Country> requestUpdate = new HttpEntity<>(country, headers);

        ResponseEntity<String> responsePut = testRestTemplate.exchange(url, HttpMethod.PUT, requestUpdate, String.class);

        System.out.println(responsePut);
        assertThat(responsePut.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));

    }


    @Test
    public void testFindCountryByName() {
        String url = site + "/api/country?name=";
        String countryToFind = "Россия";

        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = testRestTemplate.exchange(
                url + countryToFind,
                HttpMethod.GET,
                entity,
                String.class);

        Type listType = new TypeToken<ArrayList<Country>>() {
        }.getType();
        List<Country> countryList = new Gson().fromJson(response.getBody(), listType);

        assertThat(countryList, hasSize(1));
        assertThat(countryList.get(0).getName(), equalTo(countryToFind));
    }
}
