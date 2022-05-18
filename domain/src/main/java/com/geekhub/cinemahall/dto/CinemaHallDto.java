package com.geekhub.cinemahall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CinemaHallDto {

    private int id;
    private String name;
    private int capacity;
}
