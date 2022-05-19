package com.geekhub.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreateDto {

    private Long userId;
    private int placeQuantity;
    private Long eventId;
}
