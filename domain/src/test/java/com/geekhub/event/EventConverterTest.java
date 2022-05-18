package com.geekhub.event;

import com.geekhub.cinemahall.CinemaHall;
import com.geekhub.cinemahall.CinemaHallService;
import com.geekhub.event.dto.EventCreationDto;
import com.geekhub.event.dto.EventDto;
import com.geekhub.models.Genre;
import com.geekhub.models.Production;
import com.geekhub.movie.Movie;
import com.geekhub.movie.MovieService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventConverterTest {

    @Mock
    MovieService movieService;

    @Mock
    CinemaHallService cinemaHallService;

    @InjectMocks
    EventConverter eventConverter;

    @Test
    void convertFromDto_check_right_convert_object() {
        EventCreationDto dto = new EventCreationDto(1L, LocalDateTime.now(), 1, 1, 40, 100);

        Event convertedEvent = eventConverter.convertFromDto(dto);

        assertThat(convertedEvent).extracting(Event::getId).isEqualTo(dto.getId());
    }

    @Test
    void convertToEventDto_check_call_other_services() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        Movie movie = new Movie(1, "TITLE", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 9, new byte[100]);
        CinemaHall cinemaHall = new CinemaHall(1, "Hall", 60);

        when(movieService.show(event.getMovieId())).thenReturn(movie);
        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);

        eventConverter.convertToEventDto(event);

        verify(movieService).show(event.getMovieId());
        verify(cinemaHallService).getHall(event.getCinemaHallId());
    }

    @Test
    void convertToEventDto_check_set_title_and_hallName() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        Movie movie = new Movie(1, "TITLE", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 9, new byte[100]);
        CinemaHall cinemaHall = new CinemaHall(1, "Hall", 60);

        when(movieService.show(event.getMovieId())).thenReturn(movie);
        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);

        EventDto eventDto = eventConverter.convertToEventDto(event);

        assertThat(eventDto).extracting(EventDto::getMovieName).isEqualTo(movie.getTitle());
        assertThat(eventDto).extracting(EventDto::getHallName).isEqualTo(cinemaHall.getName());
    }

    @Test
    void check_that_event_converted() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        Movie movie = new Movie(1, "TITLE", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 9, new byte[100]);
        CinemaHall cinemaHall = new CinemaHall(1, "Hall", 60);

        when(movieService.show(event.getMovieId())).thenReturn(movie);
        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);

        EventDto eventDto = eventConverter.convertToEventDto(event);

        assertThat(eventDto).extracting(EventDto::getId).isEqualTo(event.getId());
    }

    @Test
    void convertToEventCreationDto__check_right_convert_object() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);

        EventCreationDto dto = eventConverter.convertToEventCreationDto(event);

        assertThat(dto).extracting(EventCreationDto::getId).isEqualTo(event.getId());
    }

    @Test
    void convertListToDto_check_result() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        List<Event> events = List.of(event);
        Movie movie = new Movie(1, "TITLE", Genre.COMEDY, "description", LocalDate.now(),
                Production.USA, List.of("actors"), "trailer", 9, new byte[100]);
        CinemaHall cinemaHall = new CinemaHall(1, "Hall", 60);

        when(movieService.show(event.getMovieId())).thenReturn(movie);
        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);

        List<EventDto> dtos = eventConverter.convertListToDto(events);

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertThat(dtos.get(0).getId()).isEqualTo(event.getId());
    }
}
