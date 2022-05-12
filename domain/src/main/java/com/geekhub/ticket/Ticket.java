package com.geekhub.ticket;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Ticket {
    private Long id;
    private String owner;
    private Long userId;
    private String movieName;
    private int placeQuantity;
    private LocalDateTime time;
    private String hall;
    private Long eventId;
    private int commonAmount;

    public Ticket(Long id, String owner, Long userId, String movieName, int placeQuantity, LocalDateTime time,
                  String hall, Long eventId, int commonAmount) {
        this.id = id;
        this.owner = owner;
        this.userId = userId;
        this.movieName = movieName;
        this.placeQuantity = placeQuantity;
        this.time = time;
        this.hall = hall;
        this.eventId = eventId;
        this.commonAmount = commonAmount;
    }

    public Ticket() {
    }

    @Override
    public String toString() {
        return "Ticket number: " + id + ", \n " +
                "\n The event will take place in the: " + hall + ", \n " +
                "\n Movie: " + movieName + ", \n" +
                "\n Time start of the movie: " + time.toString() + ", \n" +
                "\n Ticket owner is " + owner + ", \n" +
                "\n Quantity of the booking place - " + placeQuantity + ".\n" +
                "\n Common amount to pay at the cash box is - " + commonAmount + " UAH \n" +
                "\n Enjoy watching the movie!";
    }
}
