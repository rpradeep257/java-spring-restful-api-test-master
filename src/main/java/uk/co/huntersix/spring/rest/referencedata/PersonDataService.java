package uk.co.huntersix.spring.rest.referencedata;

import org.springframework.stereotype.Service;
import uk.co.huntersix.spring.rest.exception.ServiceException;
import uk.co.huntersix.spring.rest.model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.co.huntersix.spring.rest.utils.ServiceUtils.DUPLICATE_PERSON;
import static uk.co.huntersix.spring.rest.utils.ServiceUtils.FIRST_LAST_NAME_REQ;

@Service
public class PersonDataService {
    public static final List<Person> PERSON_DATA = new ArrayList<>(Arrays.asList(
        new Person("Mary", "Smith"),
        new Person("Brian", "Archer"),
        new Person("Collin", "Brown")
    ));

    public Optional<Person> findPersonByLastAndFirstName(String lastName, String firstName) {
        return PERSON_DATA.stream()
            .filter(p -> p.getFirstName().equalsIgnoreCase(firstName)
                && p.getLastName().equalsIgnoreCase(lastName))
            .findFirst();
    }

    public List<Person> findAllPersonsByLastName(String lastName) {
        return PERSON_DATA.stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
    }

    // Ideally should be passing DTO
    public Long create(Person person) {
        if (person == null || person.getFirstName().isEmpty() || person.getLastName().isEmpty()) {
            throw new ServiceException(FIRST_LAST_NAME_REQ);
        }

        if (PERSON_DATA.contains(person)) {
            throw new ServiceException(DUPLICATE_PERSON);
        }

        Person newPerson = new Person(person.getFirstName(), person.getLastName());

        PERSON_DATA.add(newPerson);

        return newPerson.getId();
    }

    public Optional<Person> findPersonById(Long id) {
        return PERSON_DATA.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }
}
