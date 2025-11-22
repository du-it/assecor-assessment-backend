package de.assecor.favouritecolor.persistence;

import de.assecor.favouritecolor.model.Color;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "persons")
public class PersonEntity implements Persistable<Long> {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String lastname;
    private String zipcode;
    private String city;

    @Enumerated(EnumType.STRING)
    private Color color;

    @Override
    public Long getId() {
        return id;
    }

    // Die IDs der CSV sind gesetzt (id != null), also sind sie NICHT neu (false).
    // Neue POST-Anfragen haben id == null, sind also NEU (true).
    @Override
    public boolean isNew() {
        return this.id == null;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

