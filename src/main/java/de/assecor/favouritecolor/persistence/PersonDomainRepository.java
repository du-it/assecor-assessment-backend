package de.assecor.favouritecolor.persistence;

import de.assecor.favouritecolor.model.Color;
import de.assecor.favouritecolor.model.Person;

import java.util.List;
import java.util.Optional;

public interface PersonDomainRepository {
    List<Person> findAll();
    Optional<Person> findById(long id);
    List<Person> findByColor(Color color);
    Person save(Person person); // Because of POST
}