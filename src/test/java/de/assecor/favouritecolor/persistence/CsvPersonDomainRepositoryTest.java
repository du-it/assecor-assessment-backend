package de.assecor.favouritecolor.persistence;

import de.assecor.favouritecolor.model.Color;
import de.assecor.favouritecolor.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test", inheritProfiles = false)
class CsvPersonDomainRepositoryTest {

    @Autowired
    private CsvPersonDomainRepository csvPersonRepository;

    @Test
    void findAll_shouldReturnAllLoadedPersons() {
        List<Person> persons = csvPersonRepository.findAll();

        // Prüfe, ob die Daten vom CsvLoader erfolgreich geladen wurden
        assertNotNull(persons);
        assertFalse(persons.isEmpty(), "Das Repository sollte Daten aus der CSV geladen haben.");
    }

    @Test
    void findById_shouldReturnCorrectPerson() {
        long existingId = 1L;

        Optional<Person> person = csvPersonRepository.findById(existingId);

        assertTrue(person.isPresent());
        assertEquals(existingId, person.get().id());
    }

    @Test
    void findById_shouldReturnEmptyOptionalForMissingId() {
        long nonExistingId = 999L;

        Optional<Person> person = csvPersonRepository.findById(nonExistingId);

        assertFalse(person.isPresent());
    }

    @Test
    void findByColorName_shouldReturnFilteredList() {
        Color targetColor = Color.VIOLETT;

        List<Person> redPersons = csvPersonRepository.findByColor(targetColor);

        assertFalse(redPersons.isEmpty(), "Sollte Personen mit der Farbe ROT finden.");

        // Prüft, ob wirklich nur Personen mit der Zielfarbe enthalten sind
        assertTrue(redPersons.stream().allMatch(p -> p.color() == (targetColor)));
    }
}
