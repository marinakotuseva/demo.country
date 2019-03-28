package ru.iteranet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import ru.iteranet.Entity.Country;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CountryControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void test_country_controller(){

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
}
