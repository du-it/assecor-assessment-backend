package de.assecor.favouritecolor.service;

import de.assecor.favouritecolor.model.Color;
import de.assecor.favouritecolor.model.Person;
import de.assecor.favouritecolor.persistence.PersonDomainRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceMockTest { // Umbenannt von UnitMockTest, wie gewünscht

    // Das Repository wird "gemockt"
    @Mock
    private PersonDomainRepository personDomainRepository;

    // Der konkrete Service wird erstellt, und das Mock wird injiziert
    @InjectMocks
    private PersonServiceImpl personService;

    // Dummy-Daten, die das Mock zurückgeben soll
    private final List<Person> DUMMY_PERSONS = List.of(
            new Person(1L, "Mustermann", "Max", "12345", "Berlin", Color.BLAU),
            new Person(2L, "Musterfrau", "Erika", "54321", "München", Color.ROT)
    );

    @Test
    void shouldReturnAllPersonsFromMockRepository() {
        when(personDomainRepository.findAll()).thenReturn(DUMMY_PERSONS);

        List<Person> result = personService.getAllPersons();

        assertEquals(2, result.size());
        assertEquals("Max", result.get(0).name());

        // Verifiziere, dass die Repository-Methode aufgerufen wurde
        verify(personDomainRepository, times(1)).findAll();
    }

    @Test
    void shouldFindPersonByExistingId() {
        long targetId = 2L;
        Person targetPerson = DUMMY_PERSONS.get(1);

        when(personDomainRepository.findById(targetId)).thenReturn(Optional.of(targetPerson));

        Optional<Person> result = personService.getPersonById(targetId);

        assertTrue(result.isPresent());
        assertEquals("Erika", result.get().name());
    }

    // ... (Weitere Tests für findByColor und non-existing ID)
}
