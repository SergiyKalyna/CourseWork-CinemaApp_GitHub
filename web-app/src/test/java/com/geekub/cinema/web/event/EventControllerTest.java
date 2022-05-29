package com.geekub.cinema.web.event;

import com.geekhub.event.Event;
import com.geekhub.event.EventConverter;
import com.geekhub.event.EventService;
import com.geekhub.event.dto.EventCreationDto;
import com.geekhub.event.dto.EventDto;
import com.geekhub.movie.MovieConverter;
import com.geekhub.movie.MovieService;
import com.geekhub.movie.dto.MovieDtoForEvent;
import com.geekhub.ticket.TicketBookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser(username = "admin", roles = "ADMIN")
class EventControllerTest {

    @Autowired
    EventController eventController;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @MockBean
    EventService eventService;

    @Autowired
    @MockBean
    MovieService movieService;

    @Autowired
    @MockBean
    TicketBookingService ticketBookingService;

    @Autowired
    @MockBean
    EventConverter eventConverter;

    @Autowired
    @MockBean
    MovieConverter movieConverter;

    @Test
    void check_get_all_events_request() throws Exception {
        List<EventDto> events = List.of(new EventDto());

        when(eventConverter.convertListToDto(eventService.getAll())).thenReturn(events);

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("event/all-events"))
                .andExpect(model().attribute("events", events));

        verify(eventConverter).convertListToDto(eventService.getAll());
    }

    @Test
    void check_delete_event_request() throws Exception {
        mockMvc.perform(post("/events/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));

        verify(eventService).deleteEvent(1L);
        verify(ticketBookingService).deleteByEventId(1L);
    }

    @Test
    void check_response_when_wrong_url() throws Exception {
        mockMvc.perform(get("/events/wrongUrl/delete"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void check_edit_event_request() throws Exception {
        EventCreationDto event = new EventCreationDto(1L, LocalDateTime.now(), 1, 1, 10, 100);
        LocalDateTime localDateTimeNow = LocalDateTime.of(LocalDate.now(),
                LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute(), 0));

        when(eventConverter.convertToEventCreationDto(eventService.getEvent(1L))).thenReturn(event);

        mockMvc.perform(get("/events/1/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("event/edit"))
                .andExpect(model().attribute("dateTimeNow", localDateTimeNow))
                .andExpect(model().attribute("event", event));

        verify(eventConverter).convertToEventCreationDto(eventService.getEvent(1L));
    }

    @Test
    void check_update_event_request() throws Exception {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 10, 100);
        EventCreationDto eventDto = new EventCreationDto(1L, LocalDateTime.now(), 1, 1, 10, 100);

        when(eventConverter.convertToEventCreationDto(eventService.getEvent(1L))).thenReturn(eventDto);
        when(eventConverter.convertFromDto(eventDto)).thenReturn(event);

        mockMvc.perform(post("/events/1/update")
                        .param("placeCost", "100")
                        .param("cinemaHallId", "1")
                        .param("time", String.valueOf(LocalDateTime.now())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));

        verify(eventService).updateEvent(1L, eventConverter.convertFromDto(eventDto));
    }

    @Test
    void check_sort_event_request() throws Exception {
        List<EventDto> events = List.of(new EventDto());

        when(eventConverter.convertListToDto(eventService.sortByHall())).thenReturn(events);

        mockMvc.perform(get("/events/sorted"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("event/all-events"))
                .andExpect(model().attribute("events", events));

        verify(eventConverter).convertListToDto(eventService.sortByHall());
    }

    @Test
    void check_create_event_request() throws Exception {
        MovieDtoForEvent movie = new MovieDtoForEvent(1, "title");
        LocalDateTime localDateTimeNow = LocalDateTime.of(LocalDate.now(),
                LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute(), 0));

        when(movieConverter.convertToDto(movieService.show(1))).thenReturn(movie);

        mockMvc.perform(get("/events/create/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("event/create"))
                .andExpect(model().attribute("movie", movie))
                .andExpect(model().attribute("dateTimeNow", localDateTimeNow));


        verify(movieConverter).convertToDto(movieService.show(1));
    }

    @Test
    void check_add_event_request() throws Exception {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 10, 100);
        EventCreationDto eventDto = new EventCreationDto(1L, LocalDateTime.now(), 1, 1, 10, 100);

        when(eventConverter.convertFromDto(eventDto)).thenReturn(event);

        mockMvc.perform(post("/events/add/1")
                        .param("placeCost", "100")
                        .param("cinemaHallId", "1")
                        .param("time", String.valueOf(LocalDateTime.now())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));

        verify(eventService).addEvent(eventConverter.convertFromDto(new EventCreationDto()));
    }

    @Test
    void check_filter_by_hall_events_request() throws Exception {
        List<EventDto> events = List.of(new EventDto());

        when(eventConverter.convertListToDto(eventService.filterByHall(1))).thenReturn(events);

        mockMvc.perform(get("/events/filterByHall")
                        .param("hall_id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("event/all-events"))
                .andExpect(model().attribute("events", events));

        verify(eventConverter).convertListToDto(eventService.filterByHall(1));
    }
}
