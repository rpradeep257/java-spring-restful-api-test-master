package uk.co.huntersix.spring.rest.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldReturnPersonDetails() throws Exception {
        assertThat(
            this.restTemplate.getForObject(
                "http://localhost:" + port + "/person/smith/mary",
                String.class
            )
        ).contains("Mary");
    }

    @Test
    public void shouldReturnPersonNotFound() throws Exception {
        ResponseEntity<ServiceResponse> responseEntity = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/person/smith/john",
                ServiceResponse.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldReturnAllPersonForLastName() throws Exception {
        ResponseEntity<ServiceResponse> responseEntity = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/person/smith",
                ServiceResponse.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getResponse()).asList().isNotEmpty().hasSize(1);
    }

    @Test
    public void shouldReturnEmptyForLastName() throws Exception {
        ResponseEntity<ServiceResponse> responseEntity = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/person/wick",
                ServiceResponse.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getResponse()).asList().isEmpty();
    }
}