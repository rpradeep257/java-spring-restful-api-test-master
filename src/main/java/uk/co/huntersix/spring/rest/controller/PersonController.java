package uk.co.huntersix.spring.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.huntersix.spring.rest.exception.ResourceNotFoundException;
import uk.co.huntersix.spring.rest.exception.ServiceException;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;
import uk.co.huntersix.spring.rest.utils.ServiceUtils;

import java.util.List;

@RestController
public class PersonController {
    private PersonDataService personDataService;

    public PersonController(@Autowired PersonDataService personDataService) {
        this.personDataService = personDataService;
    }

    @GetMapping("/person/{lastName}/{firstName}")
    public ResponseEntity<ServiceResponse> findPersonByLastAndFirstName(@PathVariable(value="lastName") String lastName,
                                 @PathVariable(value="firstName") String firstName) {
        return personDataService.findPersonByLastAndFirstName(lastName, firstName)
                .map(p -> ResponseEntity.ok(new ServiceResponse("ServiceUtils.SUCCESSFULLY_RETRIEVED", HttpStatus.OK, p)))
                .orElseThrow(() -> new ResourceNotFoundException(ServiceUtils.PERSON_NOT_FOUND));
    }

    @GetMapping("/person/{lastName}")
    public ResponseEntity<ServiceResponse> findAllPersonsByLastName(@PathVariable(value="lastName") String lastName) {

        List<Person> personsByLastName = personDataService.findAllPersonsByLastName(lastName);

        // Note: ideally should be using pagination (count, start and pageSize)
        if (personsByLastName.isEmpty()) {
            return ResponseEntity.ok(
                    new ServiceResponse(ServiceUtils.NO_MATCHING_RECORDS, HttpStatus.OK, personsByLastName));
        } else {
            return ResponseEntity.ok(
                    new ServiceResponse(ServiceUtils.SUCCESSFULLY_RETRIEVED, HttpStatus.OK, personsByLastName));
        }
    }

    @PostMapping("/person")
    public ResponseEntity<ServiceResponse> createPerson(@RequestBody Person person) {
        Long personId = personDataService.create(person);
        return new ResponseEntity(new ServiceResponse(ServiceUtils.SUCCESSFULLY_CREATED, HttpStatus.CREATED, personId),
                HttpStatus.CREATED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ServiceResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        ServiceResponse serviceResponse = new ServiceResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ServiceResponse> handleServiceException(ServiceException exception) {
        ServiceResponse serviceResponse = new ServiceResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.BAD_REQUEST);
    }

}