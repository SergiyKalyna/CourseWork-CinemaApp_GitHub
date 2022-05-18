package com.geekhub.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Long id;
    private LocalDateTime time;
    private int freePlace;
    private int placeCost;
    private String movieName;
    private String hallName;
}
