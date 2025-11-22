package de.assecor.favouritecolor.util;

import de.assecor.favouritecolor.FavouriteColorApplication;
import de.assecor.favouritecolor.model.Person;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = FavouriteColorApplication.class)
@ActiveProfiles(profiles = "", inheritProfiles = false)
public class CsvLoaderTest {
    private static final Logger log = LoggerFactory.getLogger(CsvLoaderTest.class);

    @Autowired
    private CsvLoader csvLoader;

    @Test
    void testCsvEinlesen() {
        List<Person> persons = csvLoader.getLoadedPersons();
        persons.forEach(p -> log.info(p.toString()));
        assertFalse(persons.isEmpty());
        System.out.println(persons.getFirst());
        assertEquals("Hans", persons.getFirst().name());
    }
}
