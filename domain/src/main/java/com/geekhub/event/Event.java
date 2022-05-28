package com.geekhub.event;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Event {

    private Long id;
    private LocalDateTime time;
    private int movieId;
    private int cinemaHallId;
    private int freePlace;
    private int placeCost;

    public Event() {
    }

    public Event(Long id, LocalDateTime time, int movieId, int cinemaHallId, int freePlace, int placeCost) {
        this.id = id;
        this.time = time;
        this.movieId = movieId;
        this.cinemaHallId = cinemaHallId;
        this.freePlace = freePlace;
        this.placeCost = placeCost;
    }
}
