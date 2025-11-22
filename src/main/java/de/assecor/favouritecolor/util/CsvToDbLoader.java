package de.assecor.favouritecolor.util;

import de.assecor.favouritecolor.model.Person;
import de.assecor.favouritecolor.persistence.mapper.PersonMapper;
import de.assecor.favouritecolor.persistence.JpaPersonRepository;
import de.assecor.favouritecolor.persistence.PersonDomainRepository;
import de.assecor.favouritecolor.persistence.PersonEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("db")
public class CsvToDbLoader implements CommandLineRunner {
    // CommandLineRunner werden beim Context-Laden automatisch ausgeführt.
    private static final Logger log = LoggerFactory.getLogger(CsvToDbLoader.class);

    private final JpaPersonRepository jpaRepository;
    private final CsvLoader csvLoader;
    private final PersonMapper mapper;
    private final EntityManager entityManager;

    public CsvToDbLoader(JpaPersonRepository jpaRepository, CsvLoader csvLoader,
                         PersonMapper mapper, EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.csvLoader = csvLoader;
        this.mapper = mapper;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (jpaRepository.count() == 0) {
            log.info("Lade Daten aus CSV und bereite Batch-Speicherung vor...");

            List<PersonEntity> personsToSave = csvLoader.getLoadedPersons().stream()
                    .map(mapper::toEntity)
                    .collect(Collectors.toList());

            // jpaRepository.saveAll(personsToSave); <- Probleme mit Persistable
            for (PersonEntity personToSave : personsToSave) {
                entityManager.merge(personToSave);
            }

            entityManager.flush();
            entityManager.clear();

            log.info("Datenbank-Initialisierer: {} Personen gespeichert.", jpaRepository.count());
        } else {
            log.info("Datenbank-Initialisierer: Datenbank enthält bereits Daten. Initialisierung übersprungen.");
        }
    }
}