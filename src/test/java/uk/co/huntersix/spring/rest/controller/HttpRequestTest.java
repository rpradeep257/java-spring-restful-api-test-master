package uk.co.huntersix.spring.rest.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;
import uk.co.huntersix.spring.rest.utils.ServiceUtils;

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
        assertThat(responseEntity.getBody().getMessage()).contains(ServiceUtils.NO_MATCHING_RECORDS);
    }

    @Test
    public void shouldCreateNewPerson() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");

        HttpEntity<String> request = new HttpEntity<>("{\"firstName\":\"jack\", \"lastName\":\"reacher\"}", headers);

        ResponseEntity<ServiceResponse> responseEntity = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/person",
                request,
                ServiceResponse.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody().getMessage()).contains(ServiceUtils.SUCCESSFULLY_CREATED);
    }

    @Test
    public void shouldFailCreatingDuplicatePerson() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");

        HttpEntity<String> request = new HttpEntity<>("{\"firstName\":\"jim\", \"lastName\":\"reacher\"}", headers);

        ResponseEntity<ServiceResponse> responseEntity = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/person",
                request,
                ServiceResponse.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);

        responseEntity = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/person",
                request,
                ServiceResponse.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().getMessage()).contains(ServiceUtils.DUPLICATE_PERSON);
    }

    @Test
    public void shouldFailCreatingInvalidPerson() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");

        HttpEntity<String> request = new HttpEntity<>("{\"firstName\":\"\", \"lastName\":\"reacher\"}", headers);

        ResponseEntity<ServiceResponse> responseEntity = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/person",
                request,
                ServiceResponse.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().getMessage()).contains(ServiceUtils.FIRST_LAST_NAME_REQ);
    }

    @Test
    public void shouldPatchFirstNameOfPerson() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");

        HttpEntity<String> request1 = new HttpEntity<>("{\"firstName\":\"jim\", \"lastName\":\"collin\"}", headers);
        HttpEntity<String> request2 = new HttpEntity<>("{\"firstName\":\"ray\"}", headers);

        // create a person
        ResponseEntity<ServiceResponse> responseEntity = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/person", request1,
                ServiceResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);
        Long id = Long.valueOf(responseEntity.getBody().getResponse().toString());

        // patch a person
        responseEntity = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/person/" + id + "?_method=patch", request2,
                ServiceResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

        // check if patch applied
        responseEntity = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/person/collin",
                ServiceResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getResponse()).toString().contains("ray");
    }
}