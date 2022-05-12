package com.geekhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_GATEWAY, reason="Was not input nothing in one of the fields!")
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
