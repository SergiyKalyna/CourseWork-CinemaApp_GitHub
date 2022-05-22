package com.geekhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "User with this login is present already")
public class UserAuthenticationException extends RuntimeException {

    public UserAuthenticationException(String login) {
        super(format("User with this login [%s] is present already", login));
    }
}
