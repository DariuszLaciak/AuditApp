package main.app.enums;


public enum SwotResult {
    MAXI_MAXI("W przedsiębiorstwie mocne strony mają przewagę nad słabymi, natomiast w otoczeniu szanse dominują nad " +
            "zagrożeniami. Możliwa jest strategia agresywna, polegająca na silnej ekspansji i rozwoju." +
            " Głównym celem jest korzystanie z szans, co umożliwiają mocne strony przedsiębiorstwa.", 4, "Agresywna"),
    MINI_MINI("W przedsiębiorstwie słabe strony dominują nad mocnymi, a w otoczeniu zagrożenia mają przewagę " +
            "nad szansami. Przedsiębiorstwo nie ma perspektywy rozwoju, a zalecaną strategią jest defensywna. " +
            "Koncentruje się na przetrwaniu, poprzez przeciwdziałanie dominującym słabościom i zagrożeniom, lub na" +
            " połączeniu z innym przedsiębiorstwem.", 1, "Defensywna"),
    MAXI_MINI("W przedsiębiorstwie mocne strony mają przewagę nad słabymi, ale w otoczeniu przeważają zagrożenia." +
            " W takiejsytuacji zalecana jest strategia konserwatywna, polegająca na wykorzystaniu mocnych stron w " +
            "celu pokonania zewnętrznych zagrożeń. ", 3, "Konserwatywna"),
    MINI_MAXI("W przedsiebiorstwie słabe strony mają przewagę nad mocnymi, natomiast w otoczeniu szanse dominują" +
            " nad zagrożeniami. W takiej sytuacji zalecana jest strategia konkurencyjna, polegająca na " +
            "wykorzystaniu szans oraz skupieniu się na zmniejszeniu wewnętrznych słabości firmy. Głównym " +
            "celem powinno być budowanie przewagi konkurencyjnej przedsiębiorstwa.", 2, "Konkurencyjna");

    private String description;
    private int priority;
    private String strategy;

    SwotResult(String description, int priority, String name) {
        this.description = description;
        this.priority = priority;
        this.strategy = name;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public String getStrategy() {
        return strategy;
    }
}
