package de.assecor.favouritecolor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.assecor.favouritecolor.model.Color;
import de.assecor.favouritecolor.model.Person;
import de.assecor.favouritecolor.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@WebMvcTest(PersonController.class)
public class PersonControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService personService;

    // Test 1: GET /persons
    @Test
    void shouldReturnAllPersons() throws Exception {
        // Person(id, name, lastname, zipcode, city, color)
        Person p1 = new Person(1L, "Mustermann", "Max", "12345", "Berlin", Color.BLAU);
        Person p2 = new Person(2L, "Musterfrau", "Erika", "54321", "München", Color.ROT);
        List<Person> persons = List.of(p1, p2);

        when(personService.getAllPersons()).thenReturn(persons);

        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(persons.size())))
                .andExpect(jsonPath("$[0].name", is("Max"))); // name anstatt firstName
    }

    // Test 2: GET /persons/{id}
    @Test
    void shouldReturnPersonById() throws Exception {
        // Person(id, name, lastname, zipcode, city, color)
        Person p1 = new Person(1L, "Pan", "Peter", "00000", "Nimmerland", Color.GELB);

        // Der Service gibt Optional zurück (wichtig für den Controller)
        when(personService.getPersonById(1)).thenReturn(Optional.of(p1));

        mockMvc.perform(get("/persons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastname", is("Pan")))
                .andExpect(jsonPath("$.color", is("GELB")));
    }

    @Test
    void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        when(personService.getPersonById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/persons/99"))
                .andExpect(status().isNotFound()); // Erwarte HTTP 404
    }

    // Test 3: GET /persons/color/{color}
    @Test
    void shouldReturnPersonsByColor() throws Exception {
        // Person(id, name, lastname, zipcode, city, color)
        Person p1 = new Person(3L, "Lisa", "Anna", "10001", "Köln", Color.GRUEN);
        List<Person> persons = List.of(p1);

        // Der Service erwartet hier den String-Namen der Farbe, der in der URL verwendet wird
        when(personService.getPersonsByColor(Color.GRUEN)).thenReturn(persons);

        mockMvc.perform(get("/persons/color/GRUEN")) // URL-Input: "GRUEN"
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(persons.size())))
                .andExpect(jsonPath("$[0].name", is("Anna")));
    }

    // Test 4: POST /personen (Einzel-Erstellung)
    @Test
    void shouldCreateNewPerson() throws Exception {
        // 1. Eingabedaten (Person OHNE ID, da sie neu ist)
        Person personToCreate = new Person(null, "Neu", "Mensch", "10115", "Berlin", Color.ROT);

        // 2. Erwartete Rückgabedaten (Person MIT von der DB zugewiesener ID)
        Person expectedSavedPerson = new Person(100L, "Neu", "Mensch", "10115", "Berlin", Color.ROT);

        // Mockito-Anweisung: Wenn der Service mit der "PersonToCreate" aufgerufen wird,
        // gib die "expectedSavedPerson" zurück.
        when(personService.createPerson(personToCreate)).thenReturn(expectedSavedPerson);

        // Hilfsklasse, um Java-Objekte in JSON-String zu konvertieren
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(personToCreate);

        mockMvc.perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))

                // 3. Erwartungen prüfen
                .andExpect(status().isCreated()) // Erwarte HTTP 201
                .andExpect(jsonPath("$.id", is(100))) // Prüfe die zugewiesene ID
                .andExpect(jsonPath("$.name", is("Mensch")))
                .andExpect(jsonPath("$.color", is("ROT")));
    }
}