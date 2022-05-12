package com.geekhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Cinema hall was not found by this id")
public class CinemaHallNotFoundException extends RuntimeException{

    public CinemaHallNotFoundException(int id) {
        super(format("Cinema hall with this id: [%s], was not found", id));
    }
}
