package com.geekhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Movie with this title or id was not found")
public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(int id) {
        super(format("Movie with this id [%s]  was not found", id));
    }

    public MovieNotFoundException(String cause) {
        super(format("Movies was not found. Cause [%s]", cause));
    }
}

