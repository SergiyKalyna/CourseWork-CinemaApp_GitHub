package com.geekhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

@ResponseStatus(value = HttpStatus.BAD_GATEWAY, reason = "Was input wrong login or password!")
public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException(String cause) {
        super(cause);
    }
}
