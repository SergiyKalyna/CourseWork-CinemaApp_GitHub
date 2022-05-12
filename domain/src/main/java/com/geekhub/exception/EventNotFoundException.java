package com.geekhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Event was not found by this id")
public class EventNotFoundException extends RuntimeException{

    public EventNotFoundException(Long id) {
        super(format("Event with this id: [%s], was not found", id));
    }
}
