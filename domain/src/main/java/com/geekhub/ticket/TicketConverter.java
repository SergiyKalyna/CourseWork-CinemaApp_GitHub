package com.geekhub.ticket;

import com.geekhub.cinemahall.CinemaHallService;
import com.geekhub.event.Event;
import com.geekhub.event.EventService;
import com.geekhub.movie.MovieService;
import com.geekhub.ticket.dto.TicketCreateDto;
import com.geekhub.ticket.dto.TicketDto;
import com.geekhub.user.User;
import com.geekhub.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

public class TicketConverter {

    private final MovieService movieService;
    private final CinemaHallService cinemaHallService;
    private final UserService userService;
    private final EventService eventService;

    public TicketConverter(MovieService movieService, CinemaHallService cinemaHallService, UserService userService, EventService eventService) {
        this.movieService = movieService;
        this.cinemaHallService = cinemaHallService;
        this.userService = userService;
        this.eventService = eventService;
    }

    public TicketDto convertToDto(Ticket ticket) {
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(ticket.getId());
        ticketDto.setTime(ticket.getTime());
        ticketDto.setMovieName(ticket.getMovieName());
        ticketDto.setPlaceQuantity(ticket.getPlaceQuantity());

        return ticketDto;
    }

    public Ticket convertFromDto(TicketCreateDto ticketDto) {
        User user = userService.findById(ticketDto.getUserId());
        Event event = eventService.getEvent(ticketDto.getEventId());
        Ticket ticket = new Ticket();

        ticket.setTime(event.getTime());
        ticket.setPlaceQuantity(ticketDto.getPlaceQuantity());
        ticket.setUserId(ticketDto.getUserId());
        ticket.setEventId(ticketDto.getEventId());
        ticket.setOwner(user.getFirstName() + " " + user.getSecondName());
        ticket.setMovieName(movieService.show(event.getMovieId()).getTitle());
        ticket.setHall(cinemaHallService.getHall(event.getCinemaHallId()).getName());

        return ticket;
    }

    public List<TicketDto> convertToListDto(List<Ticket> tickets) {
        return tickets.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}
