package com.geekhub.event;

import com.geekhub.cinemahall.CinemaHallService;
import com.geekhub.event.dto.EventCreationDto;
import com.geekhub.event.dto.EventDto;
import com.geekhub.movie.MovieService;

import java.util.List;
import java.util.stream.Collectors;

public class EventConverter {

    private final MovieService movieService;
    private final CinemaHallService cinemaHallService;

    public EventConverter(MovieService movieService, CinemaHallService cinemaHallService) {
        this.movieService = movieService;
        this.cinemaHallService = cinemaHallService;
    }

    public Event convertFromDto(EventCreationDto eventDto) {
        Event event = new Event();
        event.setId(eventDto.getId());
        event.setTime(eventDto.getTime());
        event.setMovieId(eventDto.getMovieId());
        event.setCinemaHallId(eventDto.getCinemaHallId());
        event.setFreePlace(eventDto.getFreePlace());
        event.setPlaceCost(eventDto.getPlaceCost());

        return event;
    }

    public EventDto convertToEventDto(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setTime(event.getTime());
        eventDto.setFreePlace(event.getFreePlace());
        eventDto.setPlaceCost(event.getPlaceCost());
        eventDto.setMovieName(movieService.show(event.getMovieId()).getTitle());
        eventDto.setHallName(cinemaHallService.getHall(event.getCinemaHallId()).getName());

        return eventDto;
    }

    public EventCreationDto convertToEventCreationDto(Event event) {
        EventCreationDto eventCreationDto = new EventCreationDto();
        eventCreationDto.setId(event.getId());
        eventCreationDto.setTime(event.getTime());
        eventCreationDto.setMovieId(event.getMovieId());
        eventCreationDto.setCinemaHallId(event.getCinemaHallId());
        eventCreationDto.setFreePlace(event.getFreePlace());
        eventCreationDto.setPlaceCost(event.getPlaceCost());

        return eventCreationDto;
    }

    public List<EventDto> convertListToDto(List<Event> events) {
        return events.stream().map(this::convertToEventDto).collect(Collectors.toList());
    }
}
