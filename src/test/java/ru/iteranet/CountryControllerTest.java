package ru.iteranet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.xml.bind.v2.TODO;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteranet.Entity.Country;
import ru.iteranet.Exceptions.NotFoundException;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void can_read_countries(){

        ResponseEntity<String> response = testRestTemplate.getForEntity("/country", String.class);

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
    public void can_find_country_by_id() {


        ResponseEntity<String> response = testRestTemplate.getForEntity("/country/1", String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(response.getBody(), type);

        assertThat(createdCountry.getName(), equalTo("Россия"));
    }

    @Test
    public void can_create_country(){

        Country country = new Country("Абхазия");
        ResponseEntity<String> response = testRestTemplate.postForEntity("/country", country, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Type type = new TypeToken<Country>() {
        }.getType();
        Country createdCountry = new Gson().fromJson(response.getBody(), type);

        assertThat(createdCountry.getName(), equalTo("Абхазия"));
    }


    //@Ignore
    @Test
    // TODO
    public void can_delete_country() {

        // TODO: response?
        //testRestTemplate.delete("/country/0");
        String url = "http://localhost:8080/country/{id}";
        String id = "1";

        testRestTemplate.delete(url, id);

//        ResponseEntity<Integer> result = testRestTemplate.exchange(url,
//                HttpMethod.DELETE,
//                HttpEntity.EMPTY,
//                Integer.class,
//                id);

        //assertThat(result.getBody(), equalTo(2));


//        Map<String, String> params = new HashMap<String, String>();
//        params.put("id","0");
//        ResponseEntity<Integer> result = testRestTemplate.exchange("/country/{id}",
//                HttpMethod.DELETE,
//                HttpEntity.EMPTY,
//                Integer.class,
//                params);
//
//        assertThat(result.getBody(), equalTo(2));
    }

    @Test
    public void can_update_country() throws URISyntaxException {

        Country country = new Country("Страна1");

        String url = "http://localhost:8080/country/1";

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Country> requestUpdate = new HttpEntity<>(country, headers);

        testRestTemplate.exchange(url, HttpMethod.PUT, requestUpdate, Country.class);

        ResponseEntity<String> response2 = testRestTemplate.getForEntity("/country", String.class);
        assertThat(response2.getStatusCode(), equalTo(HttpStatus.OK));

        Type listType = new TypeToken<ArrayList<Country>>() {
        }.getType();
        List<Country> countryList = new Gson().fromJson(response2.getBody(), listType);

        assertThat(countryList, hasSize(3));

        Country country1 = countryList.get(0);
        assertThat(country1.getName(), equalTo("Страна1"));
        Country country2 = countryList.get(1);
        assertThat(country2.getName(), equalTo("Бразилия"));

    }


    //@Test
    // todo
//    public void can_find_country_by_name() {
//
//        List<Country> countriesRussia = repository.findByName("Россия");
//
//        assertEquals(1, countriesRussia.size());
//        assertEquals("Россия", countriesRussia.get(0).getName());
//    }
}
