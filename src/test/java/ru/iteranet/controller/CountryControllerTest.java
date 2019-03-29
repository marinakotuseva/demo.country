package ru.iteranet.Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteranet.Entity.Country;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CountryControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testReadCountries(){

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country", String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Type listType = new TypeToken<ArrayList<Country>>() {
        }.getType();
        List<Country> countryList = new Gson().fromJson(response.getBody(), listType);

        assertThat(countryList, hasSize(3));

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
    public void testCreateCountry(){

        Country country = new Country("Абхазия");
        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(response.getBody(), type);

        assertThat(createdCountry.getName(), equalTo("Абхазия"));
    }

    @Test
    public void testCantCreateCountryTwoTimes(){

        Country country = new Country("Абхазия");
        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/country", country, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));


        Country country2 = new Country("Абхазия");
        ResponseEntity<String> response2 = testRestTemplate.postForEntity("/api/country", country, String.class);

        System.out.println(response2);
        assertThat(response2.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void testDeleteCountry() {

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country", String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Type listType = new TypeToken<ArrayList<Country>>() {
        }.getType();
        List<Country> countryList = new Gson().fromJson(response.getBody(), listType);

        assertThat(countryList, hasSize(3));


        ResponseEntity<Country> responseDelete = testRestTemplate.exchange("/api/country/1",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Country.class);
        System.out.println(responseDelete);
        assertThat(responseDelete.getStatusCode(), equalTo(HttpStatus.OK));

        ResponseEntity<String> responseAfterDeletetion = testRestTemplate.getForEntity("/api/country", String.class);

        assertThat(responseAfterDeletetion.getStatusCode(), equalTo(HttpStatus.OK));

        Type listType2 = new TypeToken<ArrayList<Country>>() {
        }.getType();
        List<Country> countryList2 = new Gson().fromJson(responseAfterDeletetion.getBody(), listType2);

        assertThat(countryList2, hasSize(2));

    }

    @Test
    public void testUpdateCountry() {

        String newName = "Страна1";

        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/country", String.class);
        Type listType = new TypeToken<ArrayList<Country>>() {
        }.getType();
        List<Country> countryList = new Gson().fromJson(response.getBody(), listType);
        assertThat(countryList, hasSize(3));

        Country country = new Country(newName);

        String url = "http://localhost:8080/api/country/1";

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Country> requestUpdate = new HttpEntity<>(country, headers);

        testRestTemplate.exchange(url, HttpMethod.PUT, requestUpdate, Country.class);

        ResponseEntity<String> response2 = testRestTemplate.getForEntity("/api/country", String.class);

        Type listType2 = new TypeToken<ArrayList<Country>>() {
        }.getType();
        List<Country> countryList2 = new Gson().fromJson(response2.getBody(), listType2);

        assertThat(countryList2, hasSize(3));
        assertThat(countryList2.get(0).getName(), equalTo(newName));

    }


    @Test
    // TODO
    public void testFindCountryByName() {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/country")
                .queryParam("name", "Россия");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<String> response = testRestTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);

        System.out.println(response);

        //assertEquals(1, countriesRussia.size());
        //assertEquals("Россия", countriesRussia.get(0).getName());
    }
}
