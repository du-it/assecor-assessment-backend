package de.assecor.favouritecolor.controller;

import de.assecor.favouritecolor.model.Color;
import de.assecor.favouritecolor.model.Person;
import de.assecor.favouritecolor.persistence.JpaPersonRepository;
import de.assecor.favouritecolor.persistence.mapper.PersonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

    // Lädt den gesamten Spring Context
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    // Aktiviert das "db" Profil, um H2 und JPA zu nutzen
    @ActiveProfiles("db")
    public class PersonControllerTest {

        // Zufälligen Port, auf dem die Anwendung läuft
        @LocalServerPort
        private int port;

        // Test-Client für HTTP-Anfragen
        @Autowired
        private TestRestTemplate restTemplate;

        // JPA Repository, um die Datenbank direkt zu bereinigen/prüfen
        @Autowired
        private JpaPersonRepository jpaRepository;

        @Autowired
        private PersonMapper mapper;

        private String getBaseUrl() {
            return "http://localhost:" + port + "/persons";
        }

        // Sicherstellen, dass die Datenbank vor jedem Test leer ist
        @BeforeEach
        void setup() {
           jpaRepository.deleteAll();
        }

        // ----------------------------------------------------------------------------------
        // Testfall: POST und anschließende GET Prüfung
        // ----------------------------------------------------------------------------------

        @Test
        void shouldCreateNewPersonAndRetrieveIt() {
            // Person zum Speichern (ID muss null sein, da die DB sie generiert)
            Person personToCreate = new Person(null, "Integrations-Test", "Maria", "50667", "Köln", Color.GELB);

            // POST-Anfrage ausführen
            ResponseEntity<Person> postResponse = restTemplate.postForEntity(
                    getBaseUrl(),
                    personToCreate,
                    Person.class
            );

            // 3. ASSERTION (POST): Prüfen, ob die Anfrage erfolgreich war
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            Person createdPerson = postResponse.getBody();
            assertThat(createdPerson).isNotNull();
            // ID muss von der Datenbank zugewiesen worden sein
            assertThat(createdPerson.id()).isNotNull().isPositive();
            assertThat(createdPerson.lastname()).isEqualTo("Integrations-Test");

            // GET-Anfrage ausführen, um die Persistenz zu prüfen
            Long newId = createdPerson.id();
            ResponseEntity<Person> getResponse = restTemplate.getForEntity(
                    getBaseUrl() + "/" + newId,
                    Person.class
            );

            assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            Person retrievedPerson = getResponse.getBody();
            assertThat(retrievedPerson).isEqualTo(createdPerson); // Prüfen, ob das abgerufene Objekt identisch ist

            // Tiefenprüfung direkt in der DB
            Optional<Person> dbCheck = jpaRepository.findById(newId).map(mapper::toRecord);
            assertThat(dbCheck).isPresent();
        }

        // ----------------------------------------------------------------------------------
        // Testfall: POST mit ungültigen Daten (optional)
        // ----------------------------------------------------------------------------------

        @Test
        void shouldReturnBadRequestWhenPostingInvalidData() {
            // Person mit fehlenden/ungültigen Daten (z.B. Nachname leer)
            Person invalidPerson = new Person(null, "", "Invalid", "00000", "City", Color.ROT);

            // POST-Anfrage ausführen
            ResponseEntity<String> response = restTemplate.postForEntity(
                    getBaseUrl(),
                    invalidPerson,
                    String.class
            );

            // Validierung erfolgreich und liefert Fehlercode 400?
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }