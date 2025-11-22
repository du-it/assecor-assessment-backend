package de.assecor.favouritecolor.service;

import de.assecor.favouritecolor.model.Color;
import de.assecor.favouritecolor.model.Person;
import de.assecor.favouritecolor.persistence.PersonDomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonDomainRepository personDomainRepository;

    @Autowired
    public PersonServiceImpl(PersonDomainRepository personDomainRepository) {
        this.personDomainRepository = personDomainRepository;
    }

    @Override
    public List<Person> getAllPersons() {
        return personDomainRepository.findAll();
    }

    @Override
    public Optional<Person> getPersonById(long id) {
        return personDomainRepository.findById(id);
    }

    @Override
    public List<Person> getPersonsByColor(Color color) {
        return personDomainRepository.findByColor(color);
    }

    @Override
    public Person createPerson(Person person) {
        return personDomainRepository.save(person);
    }
}