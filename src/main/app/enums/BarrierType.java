package main.app.enums;


public enum BarrierType {
    COST("Bariery kosztowe"), KNOWLEDGE("Bariery dotyczące wiedzy"), MARKET("Bariery rynkowe"), INSTITUTIONAL("Bariery instytucyjne"), OTHER("Inne powody nieprowadzenia działalności innowacyjnej");

    private String display;

    BarrierType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
