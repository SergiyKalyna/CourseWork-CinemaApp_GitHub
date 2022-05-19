package com.geekhub.ticket;

import com.geekhub.cinemahall.CinemaHall;
import com.geekhub.cinemahall.CinemaHallService;
import com.geekhub.event.Event;
import com.geekhub.event.EventService;
import com.geekhub.models.Gender;
import com.geekhub.models.Genre;
import com.geekhub.models.Production;
import com.geekhub.models.Role;
import com.geekhub.movie.Movie;
import com.geekhub.movie.MovieService;
import com.geekhub.ticket.dto.TicketCreateDto;
import com.geekhub.ticket.dto.TicketDto;
import com.geekhub.user.User;
import com.geekhub.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketConverterTest {

    @Mock
    EventService eventService;

    @Mock
    UserService userService;

    @Mock
    MovieService movieService;

    @Mock
    CinemaHallService cinemaHallService;

    @InjectMocks
    TicketConverter ticketConverter;

    @Test
    void convertToDto_check_return_right_result() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 1L, 100);

        TicketDto ticketDto = ticketConverter.convertToDto(ticket);

        assertThat(ticketDto).extracting(TicketDto::getId).isEqualTo(ticket.getId());
        assertThat(ticketDto).extracting(TicketDto::getMovieName).isEqualTo(ticket.getMovieName());
        assertThat(ticketDto).extracting(TicketDto::getPlaceQuantity).isEqualTo(ticket.getPlaceQuantity());
        assertThat(ticketDto).extracting(TicketDto::getTime).isEqualTo(ticket.getTime());
    }

    @Test
    void check_convert_to_list_dto() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 1L, 100);
        List<Ticket> tickets = List.of(ticket);

        List<TicketDto> ticketsDto = ticketConverter.convertToListDto(tickets);

        assertEquals(1, ticketsDto.size());
        assertThat(ticketsDto.get(0)).extracting(TicketDto::getId).isEqualTo(ticket.getId());
        assertThat(ticketsDto.get(0)).extracting(TicketDto::getMovieName).isEqualTo(ticket.getMovieName());
        assertThat(ticketsDto.get(0)).extracting(TicketDto::getPlaceQuantity).isEqualTo(ticket.getPlaceQuantity());
        assertThat(ticketsDto.get(0)).extracting(TicketDto::getTime).isEqualTo(ticket.getTime());
    }

    @Test
    void convertFromDto_check_call_another_services() {
        TicketCreateDto ticketDto = new TicketCreateDto(1L, 1, 1L);
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 6, 100);
        CinemaHall cinemaHall = new CinemaHall(1, "Hall", 60);
        Movie movie = new Movie(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);

        when(userService.findById(1L)).thenReturn(user);
        when(eventService.getEvent(1L)).thenReturn(event);
        when(cinemaHallService.getHall(1)).thenReturn(cinemaHall);
        when(movieService.show(1)).thenReturn(movie);

        ticketConverter.convertFromDto(ticketDto);

        verify(userService).findById(1L);
        verify(eventService).getEvent(1L);
        verify(cinemaHallService).getHall(1);
        verify(movieService).show(1);
    }

    @Test
    void check_right_convert_object_from_dto() {
        TicketCreateDto ticketDto = new TicketCreateDto(1L, 1, 1L);
        User user = new User(1L, "name", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 6, 100);
        CinemaHall cinemaHall = new CinemaHall(1, "Hall", 60);
        Movie movie = new Movie(1, "title", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 1, new byte[100]);

        when(userService.findById(1L)).thenReturn(user);
        when(eventService.getEvent(1L)).thenReturn(event);
        when(cinemaHallService.getHall(1)).thenReturn(cinemaHall);
        when(movieService.show(1)).thenReturn(movie);

        Ticket ticket = ticketConverter.convertFromDto(ticketDto);

        assertThat(ticket).extracting(Ticket::getEventId).isEqualTo(ticketDto.getEventId());
        assertThat(ticket).extracting(Ticket::getMovieName).isEqualTo(movie.getTitle());
        assertThat(ticket).extracting(Ticket::getHall).isEqualTo(cinemaHall.getName());
        assertThat(ticket).extracting(Ticket::getTime).isEqualTo(event.getTime());
        assertThat(ticket).extracting(Ticket::getUserId).isEqualTo(user.getId());
    }
}
