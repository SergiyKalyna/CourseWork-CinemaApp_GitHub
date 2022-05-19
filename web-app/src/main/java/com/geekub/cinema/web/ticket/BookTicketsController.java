package com.geekub.cinema.web.ticket;

import com.geekhub.event.Event;
import com.geekhub.event.EventConverter;
import com.geekhub.event.EventService;
import com.geekhub.event.dto.EventDto;
import com.geekhub.movie.MovieConverter;
import com.geekhub.movie.MovieService;
import com.geekhub.ticket.TicketBookingService;
import com.geekhub.ticket.TicketConverter;
import com.geekhub.ticket.dto.TicketCreateDto;
import com.geekhub.ticket.dto.TicketDto;
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
    private final TicketConverter ticketConverter;
    private final MovieConverter movieConverter;
    private final EventConverter eventConverter;

    public BookTicketsController(TicketBookingService ticketBookingService, MovieService movieService, EventService eventService, TicketConverter ticketConverter, MovieConverter movieConverter, EventConverter eventConverter) {
        this.ticketBookingService = ticketBookingService;
        this.movieService = movieService;
        this.eventService = eventService;
        this.ticketConverter = ticketConverter;
        this.movieConverter = movieConverter;
        this.eventConverter = eventConverter;
    }

    @GetMapping()
    @PreAuthorize("hasRole('USER')")
    public String getTickets(@AuthenticationPrincipal User user, Model model) {
        List<TicketDto> ticketsDto =
                ticketConverter.convertToListDto(ticketBookingService.getTicketsByUserId(user.getId()));
        model.addAttribute("tickets", ticketsDto);

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

        model.addAttribute("movie", movieConverter.convertToDto(movieService.show(movieId)));

        List<Event> events = eventService.findEventsByMovieId(movieId);
        List<EventDto> actualEvent = eventConverter.convertListToDto(events)
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

        TicketCreateDto ticketDto = new TicketCreateDto();
        ticketDto.setUserId(user.getId());
        ticketDto.setPlaceQuantity(Integer.parseInt(place));
        ticketDto.setEventId(eventId);

        ticketBookingService.bookTicket(ticketConverter.convertFromDto(ticketDto));

        return "redirect:/tickets";
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('USER')")
    public void downloadTicket(HttpServletResponse response,
                               @PathVariable("id") Long id) {
        ticketBookingService.downloadTicket(id);
        TicketDto ticket = ticketConverter.convertToDto(ticketBookingService.getTicket(id));
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
