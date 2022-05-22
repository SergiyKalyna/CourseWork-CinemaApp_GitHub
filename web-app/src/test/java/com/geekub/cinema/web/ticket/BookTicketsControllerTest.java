package com.geekub.cinema.web.ticket;

import com.geekhub.event.Event;
import com.geekhub.event.EventConverter;
import com.geekhub.event.EventService;
import com.geekhub.models.Gender;
import com.geekhub.models.Role;
import com.geekhub.movie.MovieConverter;
import com.geekhub.movie.MovieService;
import com.geekhub.movie.dto.MovieDtoForEvent;
import com.geekhub.ticket.TicketBookingService;
import com.geekhub.ticket.TicketConverter;
import com.geekhub.ticket.dto.TicketCreateDto;
import com.geekhub.ticket.dto.TicketDto;
import com.geekhub.user.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = "ADMIN")
class BookTicketsControllerTest {

    @Autowired
    BookTicketsController bookTicketsController;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @MockBean
    TicketBookingService ticketBookingService;

    @Autowired
    @MockBean
    MovieService movieService;

    @Autowired
    @MockBean
    EventService eventService;

    @Autowired
    @MockBean
    TicketConverter ticketConverter;

    @Autowired
    @MockBean
    MovieConverter movieConverter;

    @Autowired
    @MockBean
    EventConverter eventConverter;

    @Test
    void getTickets_check_request() throws Exception {
        User user = new User(1L, "login", "password", Role.USER, "first name",
                "second name", Gender.MALE, LocalDate.now());
        TicketDto ticketDto = new TicketDto(1L, "movie name", 1, LocalDateTime.now());
        List<TicketDto> ticketsDto = List.of(ticketDto);

        when(ticketConverter.convertToListDto(ticketBookingService.getTicketsByUserId(user.getId()))).thenReturn(ticketsDto);

        mockMvc.perform(get("/tickets")
                        .with(user(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("tickets/show"))
                .andExpect(model().attribute("tickets", ticketsDto));
    }

    @Test
    void deleteTicket_when_wrong_url() throws Exception {
        mockMvc.perform(post("/tickets/wrongUrl"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteTicket_check_request() throws Exception {
        mockMvc.perform(post("/tickets/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tickets"));

        verify(ticketBookingService).deleteTicket(1L);
    }

    @Test
    void bookTicket_check_view() throws Exception {
        List<Event> events = Collections.emptyList();

        when(movieConverter.convertToDto(movieService.show(1))).thenReturn(new MovieDtoForEvent(1, "title"));
        when(eventService.findEventsByMovieId(1)).thenReturn(events);
        when(eventConverter.convertListToDto(events)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tickets/book/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("tickets/book"))
                .andExpect(model().attribute("movie", new MovieDtoForEvent(1, "title")))
                .andExpect(model().attribute("events", Collections.emptyList()));
    }

    @Test
    void addTicket_check_success_book() throws Exception {
        mockMvc.perform(post("/tickets/add/1")
                        .param("place", "1")
                        .with(user(new User(1L, "login", "password", Role.USER, "first name",
                                "second name", Gender.MALE, LocalDate.now()))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tickets"));

        verify(ticketBookingService).bookTicket(ticketConverter.convertFromDto(new TicketCreateDto()));
    }

    @Test
    void downloadTicket_check_success_download() throws Exception {
        TicketDto ticket = new TicketDto(1L, "movie name", 1, LocalDateTime.now());

        when(ticketConverter.convertToDto(ticketBookingService.getTicket(1L))).thenReturn(ticket);

        mockMvc.perform(get("/tickets/1/download"))
                .andExpect(status().isOk());

    }
}
