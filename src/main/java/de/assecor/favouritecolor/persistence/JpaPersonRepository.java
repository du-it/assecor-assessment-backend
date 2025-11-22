package de.assecor.favouritecolor.persistence;

import de.assecor.favouritecolor.model.Color;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("db")
public interface JpaPersonRepository extends JpaRepository<PersonEntity, Long> {
    // Liefert die höchste ID zurück
    @Query("SELECT MAX(p.id) FROM PersonEntity p")
    Long findMaxId();
    List<PersonEntity> findByColor(Color color);
}