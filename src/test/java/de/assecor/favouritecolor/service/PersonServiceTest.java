package de.assecor.favouritecolor.service;

import de.assecor.favouritecolor.model.Person;
import de.assecor.favouritecolor.model.Color;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(profiles = "test", inheritProfiles = false)
class PersonServiceTest {
    // Die echte Implementierung des Services wird injiziert
    @Autowired
    private PersonService personService;

    // Annahme: Deine CSV-Datei enthält mindestens 3 Personen, eine davon ist BLAU.

    @Test
    void shouldReturnAllPersonsFromRealCsv() {
        // CSV-Datei über das echte Repository laden ...
        List<Person> result = personService.getAllPersons();

        assertNotNull(result);
        assertTrue(result.size() >= 1, "Es sollten Personen aus der CSV geladen werden.");
    }

    @Test
    void shouldFindPersonByRealExistingId() {
        long existingId = 1L;

        Optional<Person> result = personService.getPersonById(existingId);

        assertTrue(result.isPresent());
        assertEquals(existingId, result.get().id());
    }

    @Test
    void shouldFindPersonsByRealColor() {
        Color targetColorName = Color.BLAU;

        List<Person> result = personService.getPersonsByColor(targetColorName);

        assertFalse(result.isEmpty(), "Es sollten Personen mit der Farbe BLAU gefunden werden.");
        // Wirklich nur Personen mit gesuchter Farbe?
        assertTrue(result.stream().allMatch(p -> p.color().equals(Color.BLAU)));
    }
}
