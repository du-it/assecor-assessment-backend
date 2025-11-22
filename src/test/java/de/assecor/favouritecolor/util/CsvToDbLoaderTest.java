package de.assecor.favouritecolor.util;

import de.assecor.favouritecolor.persistence.JpaPersonRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test", "db"}) // Lade die für diesen Test nötigen Profile
                                // (application-<profile>.properties, Konfigurationen, Komponenten).
public class CsvToDbLoaderTest {

    @Autowired
    private JpaPersonRepository jpaRepository;

    @Autowired
    private CsvLoader csvLoader;

    @Test
    //@Transactional
    //@Rollback(true)
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    public void databaseShouldBeInitializedWithCsvData() {
        // Wieviel Datensätze wurden aus der CSV gelesen?
        long expectedCount = csvLoader.getLoadedPersons().size();

        // Prüfe, ob die Daten vom Loader erfolgreich in die DB geschrieben wurden
        long actualCount = jpaRepository.count();

        // 💡 Assertion: Die Anzahl der gespeicherten Entitäten muss mit der CSV-Zeilenanzahl übereinstimmen
        assertThat(actualCount)
                .isEqualTo(expectedCount)
                .as("Der CsvToDbLoader hat die falsche Anzahl von Personen in die Datenbank geladen.");

        // Optional: Zusätzliche Prüfung auf einen bestimmten Datensatz
        // PersonEntity entity = jpaRepository.findById(1L).orElse(null);
        // assertThat(entity).isNotNull();
        // assertThat(entity.getName()).isEqualTo("Max Mustermann");
    }
}