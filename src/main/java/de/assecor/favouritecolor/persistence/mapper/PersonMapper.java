package de.assecor.favouritecolor.persistence.mapper;

import de.assecor.favouritecolor.persistence.PersonEntity;
import de.assecor.favouritecolor.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List; // Muss importiert werden, falls du die Listen-Methoden behalten willst

@Mapper(componentModel = "spring")
public interface PersonMapper {

    /**
     * Mappt den Person Record (Domain/CSV) zur PersonEntity (JPA).
     */
    @Mapping(source = "lastname", target = "lastname")
    @Mapping(source = "name", target = "name")
    PersonEntity toEntity(Person record);

    /**
     * Mappt die PersonEntity (aus der DB) zurück zum Person Record (Domain).
     */
    @Mapping(source = "lastname", target = "lastname")
    @Mapping(source = "name", target = "name")
    Person toRecord(PersonEntity entity);

    /**
     * Mappt eine Liste von Records zu Entities.
     * MapStruct implementiert dies automatisch, wenn toEntity(Person) existiert.
     */
    List<PersonEntity> toEntities(List<Person> records);

    /**
     * Mappt eine Liste von Entities zu Records.
     * MapStruct implementiert dies automatisch, wenn toRecord(PersonEntity) existiert.
     */
    List<Person> toRecords(List<PersonEntity> entities);
}