package de.assecor.favouritecolor.persistence;

import de.assecor.favouritecolor.model.Color;
import de.assecor.favouritecolor.model.Person;
import de.assecor.favouritecolor.util.CsvLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("!db")
public class CsvPersonDomainRepository implements PersonDomainRepository {
    private static final Logger log = LoggerFactory.getLogger(CsvPersonDomainRepository.class);
    // Evtl. Map für schnelleren Zugriff?
    private final List<Person> persons;

    public CsvPersonDomainRepository(CsvLoader csvLoader) {
        // Problem: Wenn die CSV riesig ist, könnte es zum OutOfMemory führen.
        this.persons = csvLoader.getLoadedPersons();
        log.debug("Vom CsvLoader geladenen Pesonen ...");
        persons.forEach(p -> log.debug(p.toString()));
    }

    @Override
    public List<Person> findAll() {
        return persons;
    }

    @Override
    public Optional<Person> findById(long id) {
        return persons.stream().filter(p -> p.id() == id).findFirst();
    }

    @Override
    public List<Person> findByColor(Color color) {
        return persons.stream()
                .filter(p -> p.color().equals(color))
                .collect(Collectors.toList());
    }

    @Override
    public Person save(Person person) {
        throw new UnsupportedOperationException("Speichern in CSV Datei nicht unterstützt. Dieses Repository ist nur für das Lesen der CSV-Daten vorgesehen.");
    }
}