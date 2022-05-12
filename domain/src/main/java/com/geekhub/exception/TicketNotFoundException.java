package com.geekhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Ticket with this id was not found")
public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(Long id) {
        super(format("Ticket with this id: [%s], was not found", id));
    }
}
