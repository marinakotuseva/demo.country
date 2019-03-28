package ru.iteranet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)

public class CountryControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void test_country_controller(){

        ResponseEntity<String> response = testRestTemplate.getForEntity("/country", String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

    }
}
