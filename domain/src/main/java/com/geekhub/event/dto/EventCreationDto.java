package com.geekhub.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreationDto {

    private Long id;
    private LocalDateTime time;
    private int movieId;
    private int cinemaHallId;
    private int freePlace;
    private int placeCost;
}
