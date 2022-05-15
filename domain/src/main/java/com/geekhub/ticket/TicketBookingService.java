package com.geekhub.ticket;

import com.geekhub.event.Event;
import com.geekhub.event.EventService;
import com.geekhub.exception.TicketNotFoundException;
import com.geekhub.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TicketBookingService {
    private static final Logger logger = LoggerFactory.getLogger(TicketBookingService.class);

    private final TicketBookingRepository ticketBookingRepository;
    private final EventService eventService;

    public TicketBookingService(TicketBookingRepository ticketBookingRepository, EventService eventService) {
        this.ticketBookingRepository = ticketBookingRepository;
        this.eventService = eventService;
    }

    public void deleteTicket(Long id) {
        Ticket ticket = ticketBookingRepository.getById(id);
        if (ticket == null) {
            throw new TicketNotFoundException(id);
        }
        ticketBookingRepository.deleteTicket(id);

        logger.info("Was deleted ticket with id - " + id);
        changeEventTicket(ticket.getEventId(), (ticket.getPlaceQuantity() * (-1)));
    }

    public List<Ticket> getTicketsByUserId(Long id) {
        logger.info("Showed all tickets for user with id -" + id);
        return ticketBookingRepository.getByUserId(id);
    }

    public Ticket getTicket(Long id) {
        Optional<Ticket> ticket = Optional.ofNullable(ticketBookingRepository.getById(id));
        logger.info("Was showed ticket with id - " + id);

        return ticket.orElseThrow(() -> new TicketNotFoundException(id));
    }

    public void bookTicket(Ticket ticket) {
        if (ticket.getPlaceQuantity() <= 0) {
            throw new ValidationException("Was not input place quantity");
        } else if (ticket.getPlaceQuantity() > eventService.getEvent(ticket.getEventId()).getFreePlace()) {
            throw new ValidationException("Was create order to more tickets than are free");
        }
        ticket.setCommonAmount(amountCounter(ticket.getPlaceQuantity(), ticket.getEventId()));
        ticketBookingRepository.createTicked(ticket);
        changeEventTicket(ticket.getEventId(), ticket.getPlaceQuantity());

        logger.info("Was booked ticket for event with id - " + ticket.getEventId());
    }

    public void downloadTicket(Long id) {
        Ticket ticket = ticketBookingRepository.getById(id);
        if (ticket == null) {
            throw new TicketNotFoundException(id);
        }
        String ticketText = ticket.toString();

        try (BufferedOutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream("ticket.txt"))) {
            outputStream.write(ticketText.getBytes(StandardCharsets.UTF_8));
            logger.info("Was downloaded ticket with id - " + id);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void changeEventTicket(Long eventId, int ticketQuantity) {
        Event event = eventService.getEvent(eventId);
        event.setFreePlace(event.getFreePlace() - ticketQuantity);

        eventService.updateEvent(eventId, event);
    }

    private int amountCounter(int ticketQuantityPlace, Long eventId) {
        Event event = eventService.getEvent(eventId);
        int eventPlaceCost = event.getPlaceCost();

        logger.info("Was counted finally amount to event with id -" + eventId);

        return ticketQuantityPlace * eventPlaceCost;
    }

    public void deleteByEventId(Long eventId) {
        ticketBookingRepository.deleteTicketByEventId(eventId);
        logger.info("Was deleted all tickets on event with id - " + eventId);
    }
}
