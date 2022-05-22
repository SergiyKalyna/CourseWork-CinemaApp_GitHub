package com.geekhub.models;

import lombok.Getter;

@Getter
public enum Production {
    USA("USA"),
    BELGIUM("Belgium"),
    ITALY("Italy"),
    UKRAINE("Ukraine"),
    GERMANY("Germany"),
    SPAIN("Spain"),
    FRANCE("France");

    private final String production;

    Production(String production) {
        this.production = production;
    }

    @Override
    public String toString() {
        return production;
    }
}
