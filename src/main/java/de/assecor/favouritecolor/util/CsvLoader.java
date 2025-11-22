package de.assecor.favouritecolor.util;

import de.assecor.favouritecolor.model.Color;
import de.assecor.favouritecolor.model.Person;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class CsvLoader {
    private static final Logger log = LoggerFactory.getLogger(CsvLoader.class);

    @Value("${app.csv.filename:/persons.csv}")
    private String csvFilename;

    private List<Person> loadedPersons;

    @PostConstruct
    public void init() {
        if (csvFilename == null || csvFilename.isEmpty()) {
            throw new IllegalArgumentException("CSV filename cannot be empty or null.");
        } else {
            log.debug(csvFilename + " loaded.");
        }
        this.loadedPersons = loadPersons(csvFilename);
        loadedPersons.forEach( p -> log.debug(p.toString()));
        log.info("CsvLoader: CSV-Datei '" + csvFilename + "' geladen. Personen: " + loadedPersons.size());
    }

    /**
     * Lädt die Personen aus der CSV-Datei.
     * Nutzt eine manuelle while-Schleife, um Records zu puffern, die aufgrund
     * interner Zeilenumbrüche unvollständig sind (Multi-Line-Records). Dies behebt
     * den Fehler, bei dem Bart, Bertram über zwei Zeilen verteilt ist.
     *
     * @param source Der Dateiname der CSV-Datei.
     * @return Eine Liste der geparsten Person-Objekte.
     */
    private List<Person> loadPersons(final String source) {
        log.debug("Loading persons from " + source);
        List<Person> persons = new ArrayList<>();
        AtomicLong idCounter = new AtomicLong(1);

        try (InputStream inputStream = getClass().getResourceAsStream(source)) {
            if (inputStream == null) {
                throw new RuntimeException("CSV-Datei nicht gefunden: " + source);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                String line;
                // Buffer zum Speichern unvollständiger oder zusammengeführter Zeilen
                StringBuilder currentRecordBuffer = new StringBuilder();

                // Manuelle Schleife, die zeilenweise liest
                while ((line = reader.readLine()) != null) {

                    // Zeile bereinigen (führende/abschließende Leerzeichen)
                    String trimmedLine = line.trim();

                    // Wenn die Zeile leer ist, überspringen.
                    if (trimmedLine.isEmpty()) {
                        continue;
                    }

                    // Hänge die neue Zeile an den Puffer an. Bei unvollständigen Records
                    // wird hier die nächste Zeile einfach angehängt, ohne ein Komma einzufügen.
                    currentRecordBuffer.append(trimmedLine);

                    String fullRecordText = currentRecordBuffer.toString();

                    // Tokens separieren. Wir erwarten 4 Felder: Nachname, Vorname, PLZ Ort, Color-ID.
                    String[] tokens = fullRecordText.split(",", -1);

                    // 2. Prüfen, ob der Puffer einen vollständigen Datensatz enthält (mindestens 4 Tokens)
                    if (tokens.length >= 4) {
                        try {
                            // Record ist vollständig: Verarbeitung starten

                            String lastname = tokens[0].trim();
                            String name = tokens[1].trim();
                            String zipcodeCity = tokens[2].trim();
                            String colorToken = tokens[3].trim();

                            // PLZ und Ort extrahieren (SPLIT mit Limit 2)
                            String[] parts = zipcodeCity.split(" ", 2);
                            String zipcode = parts[0].trim();
                            String city = parts.length > 1 ? parts[1].trim() : "";

                            // 3. Sonderzeichen aus dem Ort entfernen (z.B. ☀)
                            // Nur Buchstaben (\p{L}), Zahlen (\p{N}), Bindestriche (-) und Whitespace (\s) sind erlaubt.
                            city = city.replaceAll("[^\\p{L}\\p{N}\\s]", "").trim();

                            int colorId = Integer.parseInt(colorToken);
                            Color color = Color.fromId(colorId);

                            persons.add(new Person(
                                    idCounter.getAndIncrement(),
                                    lastname,
                                    name,
                                    zipcode,
                                    city,
                                    color
                            ));

                            // Puffer nach erfolgreicher Verarbeitung leeren
                            currentRecordBuffer.setLength(0);

                        } catch (Exception e) {
                            // Fehler beim Parsen (z.B. ColorID ist kein Integer).
                            log.error("WARNUNG: Fehler beim Parsen des Records: {} - wird übersprungen. Grund: {}", fullRecordText, e.getMessage());
                            // Puffer leeren, um mit der nächsten Zeile neu zu starten.
                            currentRecordBuffer.setLength(0);
                        }
                    }
                }

                // Aufräumen: Wenn am Ende des Streams noch unvollständige Daten im Puffer sind, ignorieren wir diese.
                if (!currentRecordBuffer.isEmpty()) {
                    log.error("WARNUNG: Unvollständiger Record am Ende der Datei: {} - wird ignoriert.", currentRecordBuffer.toString());
                }

                return persons;
            }
        } catch (Exception e) {
            throw new RuntimeException("Fataler Fehler beim Einlesen der CSV: " + source, e);
        }
    }

    public List<Person> getLoadedPersons() {
        return Collections.unmodifiableList(loadedPersons);
    }
}