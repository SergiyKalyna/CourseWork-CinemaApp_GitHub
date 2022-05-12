package com.geekhub.models;

import lombok.Getter;

@Getter
public enum Gender {
    MALE ("Male"),
    FEMALE ("Female");

    private final String gender;

    Gender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return gender;
    }
}
