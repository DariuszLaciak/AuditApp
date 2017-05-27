package main.app.enums;

/**
 * Created by darek on 24.05.17.
 */
public enum SwotResult {
    MAXI_MAXI("Sytuacja dotyczy przedsiębiorstwa, w którym wewnątrz przeważają mocne strony, w otoczeniu zaś — szanse. " +
            "Takiej sytuacji odpowiada strategia maxi-maxi: silnej ekspansji i zdywersyfikowanego rozwoju. " +
            "Przykładem może być firma, która dysponując nowoczesną technologią i dużym potencjałem produkcyjnym, " +
            "może przy szybko wzrastającym rynku jednocześnie inwestować w nowe produkty i zdobywać nowe segmenty rynku.", 4, "Agresywna"),
    MINI_MINI("Firma w sytuacji WT jest pozbawiona szans rozwojowych. Działa w nieprzychylnym otoczeniu, a jej " +
            "potencjał zmian jest niewielki. Nie ma istotnych mocnych stron, które mogłaby przeciwstawić " +
            "zagrożeniom i wykorzystać do poprawienia swoich słabych stron. Strategia mini-mini sprowadza się w wersji " +
            "pesymistycznej do likwidacji, w optymistycznej zaś — do starań o przetrwanie lub połączenie się z inną organizacją.", 1, "Defensywna"),
    MAXI_MINI("W opisywanej sytuacji źródłem trudności rozwojowych firmy jest niekorzystny dla niej układ " +
            "warunków zewnętrznych. Przedsiębiorstwo może mu przeciwstawić duży potencjał wewnętrzny i próbować " +
            "przezwyciężyć zagrożenia, wykorzystując do maksimum swoje liczne mocne strony. Na przykład w warunkach " +
            "kurczącego się popytu silne przedsiębiorstwo, o dobrej pozycji konkurencyjnej, może wybrać strategię " +
            "eliminowania z rynku lub wykupienia jednego z konkurentów i przejęcia jego udziałów rynkowych.", 3, "Konserwatywna"),
    MINI_MAXI("Mamy tu do czynienia z firmą, która ma przewagę słabych stron nad mocnymi, ale sprzyja jej układ warunków zewnętrznych. " +
            "Jej strategia powinna polegać na wykorzystywaniu tych szans przy jednoczesnym zmniejszaniu lub poprawianiu " +
            "niedociągnięć wewnętrznych. Przykładem strategii mini-maxi może być dążenie firmy będącej w słabej " +
            "sytuacji finansowej do zawarcia aliansu strategicznego z innym przedsiębiorstwem w celu wykorzystania " +
            "szans związanych z otwieraniem się nowych rynków zbytu.", 2, "Konkurencyjna");

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
