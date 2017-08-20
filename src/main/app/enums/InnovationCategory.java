package main.app.enums;


public enum InnovationCategory {
    INNOVATOR_DATA("Dane innowatora"), INNOVATION_CHARACTERISTIC("Charakterystyka innowacji"), INNOVATION_LAW_STATUS("Status prawny innowacji"), INNOVATION_MARKET_VALUE("Potencjał rynkowy innowacji");

    private String display;

    InnovationCategory(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
