package com.geekhub.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User was not found")
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String login) {
        super(format("User with this login: [%s], was not found", login));
    }

    public UserNotFoundException(Long id) {
        super(format("User with this id: [%s], was not found", id));
    }
}
