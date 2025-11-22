package de.assecor.favouritecolor.controller;

import de.assecor.favouritecolor.model.Color;
import de.assecor.favouritecolor.model.Person;
import de.assecor.favouritecolor.service.PersonService;
import de.assecor.favouritecolor.service.PersonServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public List<Person> getAllPersons() {
        return personService.getAllPersons();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable int id) {
        return personService.getPersonById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/color/{color}")
    public List<Person> getPersonsByColor(@PathVariable Color color) {
        return personService.getPersonsByColor(color);
    }

    /**
     * Fügt eine neue Person zur Datenbank hinzu.
     * @param person Das zu speichernde Person-Objekt aus dem Request Body.
     * @return Die gespeicherte Person (mit der von der DB zugewiesenen ID) und HTTP 201.
     */
    @PostMapping
    public ResponseEntity<Person> addPerson(@Valid @RequestBody Person person) {
        // Wir gehen davon aus, dass die übergebene Person keine ID hat (ID=0 oder null)
        // Die saveNewPerson-Methode des Service gibt die Entity mit der neuen ID zurück
        Person savedPerson = personService.createPerson(person);

        return new ResponseEntity<>(savedPerson, HttpStatus.CREATED);
    }
}
