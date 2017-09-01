package main.app.enums;

public enum InnovationCategory {
    USER(1, "Dane innowatora", "", "", ""),
    ALL(2, "", "", "", ""),
    PRODUCT(3, "Innowacja produktowa", "to wszelkiego rodzaju zmiany polegające na udoskonaleniu  lub prowadzeniu nowej usługi lub produktu.", "produktowej", "produktową"),
    PROCESS(4, "Innowacja procesowa", "to zmiana w stosowanych przez organizację metodach wytwarzania, a także w sposobach docierania z produktem do odbiorców.", "procesowej", "procesową"),
    ORGANIZATION(5, "Innowacja organizacyjna", "nowa metoda organizacji w biznesowych praktykach firmy, organizacji miejsca pracy lub też w relacjach zewnętrznych.", "organizacyjnej", "organizacyjną"),
    MARKETING(6, "Innowacja marketingowa", "jest wprowadzaniem nowej metody marketingu włączając w to znaczące zmiany w projektowaniu produktu i opakowania, promocją produktu i strategią cenową tak długo, dopóki jest to pierwsze zastosowanie dla przedsiębiorstwa.", "marketingowej", "marketingową"),
    OTHER(7, "", "", "", "");


    private int number;
    private String display;
    private String description;
    private String conjunction1;
    private String conjunction2;

    InnovationCategory(int number, String display, String description, String conjunction1, String conjunction2) {
        this.number = number;
        this.display = display;
        this.description = description;
        this.conjunction1 = conjunction1;
        this.conjunction2 = conjunction2;
    }

    public String getDisplay() {
        return display;
    }

    public String getDescription() {
        return description;
    }

    public String getConjunction1() {
        return conjunction1;
    }

    public String getConjunction2() {
        return conjunction2;
    }

    public int getNumber() {
        return number;
    }
}
