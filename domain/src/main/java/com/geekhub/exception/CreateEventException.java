package com.geekhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Event creation time overlaps with existing event(s)")
public class CreateEventException extends RuntimeException{

    public CreateEventException(String cause) {
        super(format("Event was not created. Cause: [%s]", cause));
    }
}
