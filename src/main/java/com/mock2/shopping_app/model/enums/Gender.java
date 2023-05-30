package com.mock2.shopping_app.model.enums;

import lombok.Getter;

@Getter
public enum Gender {
    M("M", "Male"), F("F", "Female");
    private final String abbr;
    private final String value;

    Gender(String abbr, String value) {
        this.abbr = abbr;
        this.value = value;
    }
}
