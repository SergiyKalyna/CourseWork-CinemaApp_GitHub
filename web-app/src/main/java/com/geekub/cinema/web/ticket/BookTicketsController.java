package com.geekub.cinema.web.ticket;

import com.geekhub.cinemahall.CinemaHallService;
import com.geekhub.event.Event;
import com.geekhub.event.EventService;
import com.geekhub.movie.MovieService;
import com.geekhub.ticket.Ticket;
import com.geekhub.ticket.TicketBookingService;
import com.geekhub.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tickets")
public class BookTicketsController {
    private static final Logger logger = LoggerFactory.getLogger(BookTicketsController.class);

    private final TicketBookingService ticketBookingService;
    private final MovieService movieService;
    private final EventService eventService;
    private final CinemaHallService cinemaHallService;

    public BookTicketsController(TicketBookingService ticketBookingService, MovieService movieService, EventService eventService, CinemaHallService cinemaHallService) {
        this.ticketBookingService = ticketBookingService;
        this.movieService = movieService;
        this.eventService = eventService;
        this.cinemaHallService = cinemaHallService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('USER')")
    public String getTickets(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("tickets",
                ticketBookingService.getTicketsByUserId(user.getId()));

        return "tickets/show";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public String deleteTicket(@PathVariable("id") Long id) {
        ticketBookingService.deleteTicket(id);

        return "redirect:/tickets";
    }

    @GetMapping("/book/{id}")
    @PreAuthorize("hasRole('USER')")
    public String bookTicket(@PathVariable("id") int movieId, Model model) {

        model.addAttribute("movie", movieService.show(movieId));

        List<Event> events = eventService.findEventsByMovieId(movieId);
        List<Event> actualEvent = events
                .stream()
                .filter(event -> event.getTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());

        model.addAttribute("events", actualEvent);

        logger.info("Started operation of book tickets to movie with id - " + movieId);

        return "tickets/book";
    }

    @PostMapping("/add/{id}")
    @PreAuthorize("hasRole('USER')")
    public String addTicket(@PathVariable("id") Long eventId,
                            @RequestParam("place") String place,
                            @AuthenticationPrincipal User user) {

        Event event = eventService.getEvent(eventId);

        Ticket ticket = new Ticket();
        ticket.setOwner(user.getFirstName() + " " + user.getSecondName());
        ticket.setUserId(user.getId());
        ticket.setPlaceQuantity(Integer.parseInt(place));
        ticket.setTime(event.getTime());
        ticket.setEventId(event.getId());
        ticket.setHall(cinemaHallService.getHall(event.getCinemaHallId()).getName());
        ticket.setMovieName(movieService.show(event.getMovieId()).getTitle());

        ticketBookingService.bookTicket(ticket);

        return "redirect:/tickets";
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('USER')")
    public void downloadTicket(HttpServletResponse response,
                               @PathVariable("id") Long id) {
        ticketBookingService.downloadTicket(id);
        Ticket ticket = ticketBookingService.getTicket(id);
        String fileName = ticket.getMovieName() + "_" + ticket.getTime() + "_ticket.txt";
        String filePath = "./ticket.txt";

        Path file = Paths.get(filePath);
        if (Files.exists(file)) {
            response.setContentType("application/txt");
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            try {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException ex) {
               logger.error(ex.getMessage());
            }
        }
    }
}
