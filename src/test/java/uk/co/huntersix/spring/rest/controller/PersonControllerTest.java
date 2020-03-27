package uk.co.huntersix.spring.rest.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.huntersix.spring.rest.utils.ServiceUtils.DUPLICATE_PERSON;
import static uk.co.huntersix.spring.rest.utils.ServiceUtils.FIRST_LAST_NAME_REQ;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.huntersix.spring.rest.exception.ServiceException;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;
import uk.co.huntersix.spring.rest.utils.ServiceUtils;

import java.util.Arrays;
import java.util.Optional;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonDataService personDataService;

    @Test
    public void shouldReturnPersonFromService() throws Exception {
        when(personDataService.findPersonByLastAndFirstName(any(), any())).thenReturn(Optional.of(new Person("Mary", "Smith")));
        this.mockMvc.perform(get("/person/smith/mary"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("response.id").exists())
            .andExpect(jsonPath("response.firstName").value("Mary"))
            .andExpect(jsonPath("response.lastName").value("Smith"));
    }

    @Test
    public void shouldReturnPersonNotFoundFromService() throws Exception {
        when(personDataService.findPersonByLastAndFirstName(any(), any())).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/person/smith/john"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value(ServiceUtils.PERSON_NOT_FOUND));
    }

    @Test
    public void shouldReturnAllPersonForLastNameFromService() throws Exception {
        when(personDataService.findAllPersonsByLastName(any())).thenReturn(Arrays.asList(new Person("john", "wick"),
                new Person("jason", "wick")));
        this.mockMvc.perform(get("/person/wick"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value(ServiceUtils.SUCCESSFULLY_RETRIEVED))
                .andExpect(jsonPath("$.response", hasSize(2)))
                .andExpect(jsonPath("$.response.[0].firstName", is("john")))
                .andExpect(jsonPath("$.response.[0].lastName", is("wick")))
                .andExpect(jsonPath("$.response.[1].firstName", is("jason")))
                .andExpect(jsonPath("$.response.[1].lastName", is("wick")));
    }

    @Test
    public void shouldReturnEmptyListOfPersonByLastNameFromService() throws Exception {
        when(personDataService.findAllPersonsByLastName(any())).thenReturn(Arrays.asList());
        this.mockMvc.perform(get("/person/wick"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response", hasSize(0)))
                .andExpect(jsonPath("message").value(ServiceUtils.NO_MATCHING_RECORDS));
    }

    @Test
    public void shouldCreateNewPerson() throws Exception {
        when(personDataService.create(any())).thenReturn(Long.valueOf(1));
        this.mockMvc.perform(post("/person")
                .content("{\"firstName\":\"ray\", \"lastName\":\"smith\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response", is(1)));
    }

    @Test
    public void shouldFailCreatingInvalidPerson() throws Exception {
        when(personDataService.create(any())).thenThrow(new ServiceException(FIRST_LAST_NAME_REQ));
        this.mockMvc.perform(post("/person")
                .content("{\"firstName\":\"\", \"lastName\":\"smith\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(FIRST_LAST_NAME_REQ)));
    }

    @Test
    public void shouldFailCreatingDuplicatePerson() throws Exception {
        when(personDataService.create(any())).thenThrow(new ServiceException(DUPLICATE_PERSON));
        this.mockMvc.perform(post("/person")
                .content("{\"firstName\":\"ray\", \"lastName\":\"smith\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(DUPLICATE_PERSON)));
    }

    @Test
    public void shouldPatchFirstNameOfPerson() throws Exception {
        when(personDataService.findPersonById(Long.valueOf(4))).thenReturn(Optional.of(new Person("john", "smith")));
        this.mockMvc.perform(patch("/person/4")
                .content("{\"firstName\":\"ray\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Successfully updated")));
    }

    @Test
    public void shouldFailToPatchFirstNameOfPerson() throws Exception {
        when(personDataService.findPersonById(Long.valueOf(4))).thenReturn(Optional.of(new Person("john", "smith")));
        this.mockMvc.perform(patch("/person/4")
                .content("")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


}