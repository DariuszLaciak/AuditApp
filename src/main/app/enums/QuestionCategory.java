package main.app.enums;

/**
 * Created by Darek on 2017-04-22.
 */
public enum QuestionCategory {
    GENERAL("Metryki firmy"),
    STRATEGIC("Strategia firmy"),
    PROCESSES("Procesy firmy"),
    ORGANIZATION("Organizacja firmy"),
    COUPLINGS("Powiązania firmy"),
    LEARNING("Uczenie się firmy");

    private String visible;

    QuestionCategory(String visible) {
        this.visible = visible;
    }

    public String getVisible() {
        return visible;
    }
}
