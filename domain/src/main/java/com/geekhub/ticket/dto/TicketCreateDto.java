package com.geekhub.ticket.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TicketCreateDto {

    private Long userId;
    private int placeQuantity;
    private Long eventId;
}
