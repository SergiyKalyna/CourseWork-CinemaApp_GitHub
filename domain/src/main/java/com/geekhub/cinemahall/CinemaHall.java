package com.geekhub.cinemahall;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CinemaHall {

    private int id;
    private String name;
    private int capacity;

    public CinemaHall(int id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public CinemaHall() {
    }
}
