package com.geekhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "You have not a rights to this page")
public class UserHaveNotRightsException extends RuntimeException{

    public UserHaveNotRightsException(String message) {
        super(message);
    }
}
