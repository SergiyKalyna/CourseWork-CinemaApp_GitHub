package com.geekhub.cinemahall;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CinemaHallDto {

    private int id;
    private String name;
    private int capacity;
}
