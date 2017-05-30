package main.app.enums;

public enum Status {
    OPEN("Otwarte"), CLOSED("Zamknięte");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public String getValue() {
        return status;
    }
}
