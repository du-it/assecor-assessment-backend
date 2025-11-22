package de.assecor.favouritecolor.persistence;

import de.assecor.favouritecolor.model.Color;
import de.assecor.favouritecolor.model.Person;
import de.assecor.favouritecolor.persistence.mapper.PersonMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("db")
public class JpaPersonDomainRepository implements PersonDomainRepository {

    private final JpaPersonRepository jpaRepository;
    private final PersonMapper mapper;

    public JpaPersonDomainRepository(JpaPersonRepository jpaRepository, PersonMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Person> findAll() {
        // 1. Hole List<PersonEntity> aus der Datenbank
        // 2. Konvertiere sie mit dem Mapper in List<Person> Records
        return mapper.toRecords(jpaRepository.findAll());
    }

    @Override
    public Optional<Person> findById(long id) {
        // 1. Finde Optional<PersonEntity>
        // 2. Mappe es zu Optional<Person> Record
        return jpaRepository.findById(id).map(mapper::toRecord);
    }

    @Override
    public List<Person> findByColor(Color color) {
        // 1. Hole List<PersonEntity> (durch Spring Data JPA Query-Methode)
        // 2. Konvertiere sie mit dem Mapper in List<Person> Records
        return mapper.toRecords(jpaRepository.findByColor(color));
    }

    @Override
    public Person save(Person person) {
        // 1. Manuelle ID-Generierung für NEUE Personen (POST-Anfrage)
        if (person.id() == null) {

            // a) Höchste aktuelle ID abfragen
            Long maxId = jpaRepository.findMaxId();

            // b) Neue ID berechnen (Start bei 1, falls Tabelle leer)
            Long newId = (maxId == null) ? 1L : maxId + 1;

            // c) Neuen, IMMUTABLE Person Record mit der berechneten ID erstellen
            person = new Person(
                    newId,
                    person.lastname(),
                    person.name(),
                    person.zipcode(),
                    person.city(),
                    person.color()
            );
        }

        // 2. Person Record zu einer PersonEntity mappen (jetzt mit gesetzter ID,
        //    oder mit der CSV-ID, falls es ein Update wäre)
        PersonEntity entity = mapper.toEntity(person);

        // 3. Speichern über Spring Data JPA.
        //    Funktioniert, da PersonEntity:
        //    a) KEIN @GeneratedValue hat (erlaubt manuelle ID-Setzung/CSV-IDs).
        //    b) Persistable implementiert (gibt false zurück, da ID != null ist,
        //       was zu einem INSERT/Merge führt).
        PersonEntity savedEntity = jpaRepository.save(entity);

        // 4. Gespeicherte Entity zurück zum Record mappen und zurückgeben.
        return mapper.toRecord(savedEntity);
    }
}