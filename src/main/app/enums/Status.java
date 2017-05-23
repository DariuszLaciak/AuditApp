package main.app.enums;

public enum Status {
    PENDING("Oczekujące"), ACCEPTED("Zaakceptowane"), NOT_ACCEPTED("Odrzucone");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public String getValue() {
        return status;
    }
}
