package com.geekub.cinema.web.event;

import com.geekhub.event.Event;
import com.geekhub.event.EventConverter;
import com.geekhub.event.EventService;
import com.geekhub.event.dto.EventCreationDto;
import com.geekhub.movie.MovieConverter;
import com.geekhub.movie.MovieService;
import com.geekhub.ticket.TicketBookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequestMapping("/events")
public class EventController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;
    private final MovieService movieService;
    private final TicketBookingService ticketBookingService;
    private final EventConverter eventConverter;
    private final MovieConverter movieConverter;

    public EventController(EventService eventService, MovieService movieService, TicketBookingService ticketBookingService, EventConverter eventConverter, MovieConverter movieConverter) {
        this.eventService = eventService;
        this.movieService = movieService;
        this.ticketBookingService = ticketBookingService;
        this.eventConverter = eventConverter;
        this.movieConverter = movieConverter;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String getEvents(Model model) {
        var events = eventConverter.convertListToDto(eventService.getAll());

        model.addAttribute("events", events);

        return "event/all-events";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteEvent(@PathVariable("id") String id) {
        eventService.deleteEvent(Long.valueOf(id));
        ticketBookingService.deleteByEventId(Long.valueOf(id));

        return "redirect:/events";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editEvent(@PathVariable("id") String id, Model model) {
        LocalDateTime localDateTimeNow = LocalDateTime.of(LocalDate.now(),
                LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute(), 0));
        EventCreationDto eventCreationDto =
                eventConverter.convertToEventCreationDto(eventService.getEvent(Long.valueOf(id)));

        model.addAttribute("dateTimeNow", localDateTimeNow);
        model.addAttribute("event", eventCreationDto);
        logger.info("Started operation of edit event with id - " + id);

        return "event/edit";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateEvent(@PathVariable("id") Long id,
                              @RequestParam("placeCost") String placeCost,
                              @RequestParam("cinemaHallId") String cinemaHallId,
                              @RequestParam("time") String time) {
        EventCreationDto eventCreationDto =
                eventConverter.convertToEventCreationDto(eventService.getEvent(id));
        eventCreationDto.setPlaceCost(Integer.parseInt(placeCost));
        eventCreationDto.setCinemaHallId(Integer.parseInt(cinemaHallId));
        eventCreationDto.setTime(LocalDateTime.parse(time));

        eventService.updateEvent(id, eventConverter.convertFromDto(eventCreationDto));

        return "redirect:/events";
    }

    @GetMapping("/sorted")
    @PreAuthorize("hasRole('ADMIN')")
    public String sortEventByHall(Model model) {
        var events = eventConverter.convertListToDto(eventService.sortByHall());

        model.addAttribute("events", events);

        return "event/all-events";
    }

    @GetMapping("/create/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String createEvent(@PathVariable("id") int movieId, Model model) {
        LocalDateTime localDateTimeNow = LocalDateTime.of(LocalDate.now(),
                LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute(), 0));

        model.addAttribute("event", new EventCreationDto());
        model.addAttribute("movie", movieConverter.convertToDto(movieService.show(movieId)));
        model.addAttribute("dateTimeNow", localDateTimeNow);

        logger.info("Started operation of create event to movie with id - " + movieId);

        return "event/create";
    }

    @PostMapping("/add/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String addEvent(@PathVariable("id") int movieId,
                           @RequestParam("placeCost") String placeCost,
                           @RequestParam("cinemaHallId") String cinemaHallId,
                           @RequestParam("time") String time) {

        EventCreationDto eventCreationDto = new EventCreationDto();
        eventCreationDto.setMovieId(movieId);
        eventCreationDto.setPlaceCost(Integer.parseInt(placeCost));
        eventCreationDto.setCinemaHallId(Integer.parseInt(cinemaHallId));
        eventCreationDto.setTime(LocalDateTime.parse(time));

        eventService.addEvent(eventConverter.convertFromDto(eventCreationDto));

        return "redirect:/events";
    }
}
