package main.app.enums;

public enum LoginType {
    ADMIN("Admin"), USER("Manager"), EMPLOYEE("Pracownik");

    private String displayName;

    LoginType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
