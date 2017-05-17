package main.app.enums;

import main.app.Constraints;

public enum QuestionCategory {
    GENERAL("Metryki"),
    STRATEGIC("Strategia"),
    PROCESSES("Procesy"),
    ORGANIZATION("Organizacja"),
    COUPLINGS("Powiązania"),
    LEARNING("Uczenie się");

    private String visible;

    QuestionCategory(String visible) {
        this.visible = visible;
    }

    public String getVisible() {
        return visible;
    }

    public String getDesription() {
        String description = "W przygotowaniu";
        switch (this) {
            case LEARNING:
                description = Constraints.LEARNING_DESCRIPTION;
                break;
            case COUPLINGS:
                description = Constraints.COUPLINGS_DESCRIPTION;
                break;
            case PROCESSES:
                description = Constraints.PROCESSES_DESCRIPTION;
                break;
            case STRATEGIC:
                description = Constraints.STRATEGIC_DESCRIPTION;
                break;
            case ORGANIZATION:
                description = Constraints.ORGANIZATION_DESCRIPTION;
                break;
            default:
                break;
        }
        return description;
    }
}
