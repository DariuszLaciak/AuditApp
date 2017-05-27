package main.app.enums;

/**
 * Created by darek on 24.05.17.
 */
public enum SwotCategory {
    STRENGHTS("Mocne strony"), WEAKNESSES("Słabe strony"), OPPORTUNITES("Szanse w otoczeniu"), THREATS("Zagrożenia w otoczeniu");

    private String value;

    SwotCategory(String val) {
        value = val;
    }

    public String getValue() {
        return value;
    }
}
