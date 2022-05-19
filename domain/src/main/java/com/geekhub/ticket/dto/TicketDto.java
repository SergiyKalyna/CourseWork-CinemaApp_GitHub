package com.geekhub.ticket.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TicketDto {

    private Long id;
    private String movieName;
    private int placeQuantity;
    private LocalDateTime time;
}
