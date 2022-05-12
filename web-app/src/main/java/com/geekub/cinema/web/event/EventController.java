package com.geekub.cinema.web.event;

import com.geekhub.cinemahall.CinemaHallService;
import com.geekhub.event.Event;
import com.geekhub.event.EventService;
import com.geekhub.movie.MovieService;
import com.geekhub.ticket.TicketBookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;
    private final MovieService movieService;
    private final CinemaHallService cinemaHallService;
    private final TicketBookingService ticketBookingService;

    public EventController(EventService eventService, MovieService movieService, CinemaHallService cinemaHallService, TicketBookingService ticketBookingService) {
        this.eventService = eventService;
        this.movieService = movieService;
        this.cinemaHallService = cinemaHallService;
        this.ticketBookingService = ticketBookingService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String getEvents(Model model) {
        var events = eventService.getAll();
        model.addAttribute("events", getFullEventsParameters(events));

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
        model.addAttribute("event", eventService.getEvent(Long.valueOf(id)));
        logger.info("Started operation of edit event with id - " + id);

        return "event/edit";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateEvent(@PathVariable("id") Long id,
                              @RequestParam("placeCost") String placeCost,
                              @RequestParam("cinemaHallId") String cinemaHallId,
                              @RequestParam("time") String time) {
        Event event = eventService.getEvent(id);
        event.setPlaceCost(Integer.parseInt(placeCost));
        event.setCinemaHallId(Integer.parseInt(cinemaHallId));
        event.setTime(LocalDateTime.parse(time));

        eventService.updateEvent(id, event);

        return "redirect:/events";
    }

    @GetMapping("/sorted")
    @PreAuthorize("hasRole('ADMIN')")
    public String sortEventByHall(Model model) {
        var events = eventService.sortByHall();
        model.addAttribute("events", getFullEventsParameters(events));

        return "event/all-events";
    }

    @GetMapping("/create/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String createEvent(@PathVariable("id") int movieId, Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("movie", movieService.show(movieId));
        logger.info("Started operation of create event to movie with id - " + movieId);

        return "event/create";
    }

    @PostMapping("/add/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String addEvent(@PathVariable("id") int movieId,
                           @RequestParam("placeCost") String placeCost,
                           @RequestParam("cinemaHallId") String cinemaHallId,
                           @RequestParam("time") String time) {

        Event event = new Event();
        event.setMovieId(movieId);
        event.setPlaceCost(Integer.parseInt(placeCost));
        event.setCinemaHallId(Integer.parseInt(cinemaHallId));
        event.setTime(LocalDateTime.parse(time));

        eventService.addEvent(event);

        return "redirect:/events";
    }

    private List<Event> getFullEventsParameters(List<Event> events) {
        events.forEach(event -> {
            event.setMovieName(movieService.show(event.getMovieId()).getTitle());
            event.setHallName(cinemaHallService.getHall(event.getCinemaHallId()).getName());
        });

        return events;
    }
}
