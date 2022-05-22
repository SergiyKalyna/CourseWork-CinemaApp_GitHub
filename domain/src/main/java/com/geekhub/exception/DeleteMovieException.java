package com.geekhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Movie cant be deleted")
public class DeleteMovieException extends RuntimeException {
    public DeleteMovieException(String cause) {
        super(format("Movie cant be deleted. Cause: [%s]", cause));
    }
}
