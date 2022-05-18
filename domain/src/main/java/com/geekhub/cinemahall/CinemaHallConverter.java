package com.geekhub.cinemahall;

import com.geekhub.cinemahall.dto.CinemaHallDto;

import java.util.List;
import java.util.stream.Collectors;

public class CinemaHallConverter {

    public CinemaHallDto convertToDto(CinemaHall cinemaHall) {
        CinemaHallDto cinemaHallDto = new CinemaHallDto();

        cinemaHallDto.setId(cinemaHall.getId());
        cinemaHallDto.setName(cinemaHall.getName());
        cinemaHallDto.setCapacity(cinemaHall.getCapacity());

        return cinemaHallDto;
    }

    public CinemaHall convertFromDto(CinemaHallDto cinemaHallDto) {
        CinemaHall cinemaHall = new CinemaHall();

        cinemaHall.setId(cinemaHallDto.getId());
        cinemaHall.setName(cinemaHallDto.getName());
        cinemaHall.setCapacity(cinemaHallDto.getCapacity());

        return cinemaHall;
    }

    public List<CinemaHallDto> convertListToDto(List<CinemaHall> cinemaHallList) {
        return cinemaHallList.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}
