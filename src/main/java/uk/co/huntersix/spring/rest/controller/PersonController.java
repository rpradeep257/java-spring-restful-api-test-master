package uk.co.huntersix.spring.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.co.huntersix.spring.rest.exception.ResourceNotFoundException;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

@RestController
public class PersonController {
    private PersonDataService personDataService;

    public PersonController(@Autowired PersonDataService personDataService) {
        this.personDataService = personDataService;
    }

    @GetMapping("/person/{lastName}/{firstName}")
    public ResponseEntity<ServiceResponse> person(@PathVariable(value="lastName") String lastName,
                                 @PathVariable(value="firstName") String firstName) {
        return personDataService.findPerson(lastName, firstName)
                .map(p -> ResponseEntity.ok(new ServiceResponse("Successfully retrieved", HttpStatus.OK, p)))
                .orElseThrow(() -> new ResourceNotFoundException("Person not found"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ServiceResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        ServiceResponse serviceResponse = new ServiceResponse("Person not found", HttpStatus.NOT_FOUND);
        return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.NOT_FOUND);
    }
}