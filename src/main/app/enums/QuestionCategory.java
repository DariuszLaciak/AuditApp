package main.app.enums;

import main.app.Constraints;

public enum QuestionCategory {
    STRATEGIC("Strategia", QuestionType.LICKERT),
    PROCESSES("Procesy", QuestionType.LICKERT),
    ORGANIZATION("Organizacja", QuestionType.LICKERT),
    COUPLINGS("Powiązania", QuestionType.LICKERT),
    LEARNING("Uczenie się", QuestionType.LICKERT),
    STRATEGY_CHANGE("Zmiana strategii zarząrzania", QuestionType.DETAILED),
    PROJECTS("Realizacja projektów badawczo-rozwojowych", QuestionType.DETAILED),
    COMPETITIONS("Kompetencje dla innowacji", QuestionType.DETAILED),
    SEARCHING("Poszukiwanie okazji do innowacji", QuestionType.DETAILED),
    SECURITY("Bezpieczeństwo innowacyjnej własności intelektualnej", QuestionType.DETAILED),
    CASHING("Finansowanie działalności innowacyjnej", QuestionType.DETAILED),
    ASSIST("Wspieranie działalności innowacyjnej", QuestionType.DETAILED);

    private String visible;
    private QuestionType type;

    QuestionCategory(String visible, QuestionType type) {
        this.visible = visible;
        this.type = type;
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

    public QuestionType getType() {
        return type;
    }
}
