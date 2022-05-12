package com.geekhub.feedback;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Feedback {

    private Long id;
    private LocalDateTime time;
    private int movieId;
    private Long userId;
    private String userName;
    private String feedback;
    private int movieScore;


    public Feedback(Long id, LocalDateTime time, int movieId, Long userId, String userName, String feedback, int movieScore) {
        this.id = id;
        this.time = time;
        this.movieId = movieId;
        this.userId = userId;
        this.userName = userName;
        this.feedback = feedback;
        this.movieScore = movieScore;
    }

    public Feedback() {
    }
}
