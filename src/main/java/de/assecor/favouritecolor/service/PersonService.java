package de.assecor.favouritecolor.service;

import de.assecor.favouritecolor.model.Color;
import de.assecor.favouritecolor.model.Person;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    List<Person> getAllPersons();
    Optional<Person> getPersonById(long id);
    List<Person> getPersonsByColor(Color color);

    Person createPerson(Person person);
}
