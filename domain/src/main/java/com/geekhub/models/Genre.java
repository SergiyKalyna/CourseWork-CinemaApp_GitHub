package com.geekhub.models;

import lombok.Getter;

@Getter
public enum Genre {
    HORROR ("Horror"),
    COMEDY ("Comedy"),
    THRILLER ("Thriller"),
    ACTION ("Action"),
    DRAMA ("Drama"),
    DETECTIVE ("Detective"),
    CARTOON ("Cartoon"),
    FANTASY ("Fantasy");

    private final String genre;

    Genre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return genre;
    }
}
