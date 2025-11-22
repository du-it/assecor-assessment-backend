package de.assecor.favouritecolor.model;

public enum Color {
    BLAU(1, "blau"),
    GRUEN(2, "grün"),
    VIOLETT(3, "violett"),
    ROT(4, "rot"),
    GELB(5, "gelb"),
    TUERKIS(6, "türkis"),
    WEISS(7, "weiß");

    private final int id;
    private final String label;

    Color(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() { return id; }
    public String getLabel() { return label; }

    public static Color fromId(int id) {
        for (Color c : values()) {
            if (c.id == id) return c;
        }
        throw new IllegalArgumentException("Unbekannte Farb-ID: " + id);
    }
}

