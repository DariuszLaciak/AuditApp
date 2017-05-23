package main.app.enums;

public enum IdeaTypes {
    COMPANY_CLIENT("Relacja firma-klient"),
    PROMOTION("Wizerunek firmy i promocja"),
    INTERNAL("Organizacja pracy i zarządzenie, relacje wewnętrzne"),
    SECURITY("Bezpieczeństwo"),
    MACHINES("Maszyny"),
    PRODUCTION("Organizacja produkcji"),
    PRODUCT("Produkty"),
    OTHER("Inne");

    private String value;

    IdeaTypes(String s) {
        value = s;
    }

    public String getValue() {
        return value;
    }
}
