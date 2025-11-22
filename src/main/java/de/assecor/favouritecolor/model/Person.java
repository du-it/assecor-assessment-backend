package de.assecor.favouritecolor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Person(
        @JsonProperty("id")
        Long id,
        @NotBlank(message = "Nachname darf nicht leer sein")
        @JsonProperty("lastname")
        String lastname,
        @NotBlank(message = "Name darf nicht leer sein")
        @JsonProperty("name")
        String name,
        @NotBlank(message = "Postleitzahl darf nicht leer sein")
        @JsonProperty("zipcode")
        String zipcode,
        @NotBlank(message = "Stadt darf nicht leer sein")
        @JsonProperty("city")
        String city,
        @NotNull(message = "Lieblingsfarbe muss angegeben werden")
        @JsonProperty("color")
        Color color
) {}