package com.geekhub.ticket;

import com.geekhub.event.Event;
import com.geekhub.event.EventService;
import com.geekhub.exception.TicketNotFoundException;
import com.geekhub.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketBookingServiceTest {

    @Mock
    TicketBookingRepository ticketBookingRepository;

    @Mock
    EventService eventService;

    @InjectMocks
    TicketBookingService ticketBookingService;

    @Test
    void getTicketsByUserId_check_call_repository_method() {
        List<Ticket> tickets = new ArrayList<>();
        when(ticketBookingRepository.getByUserId(1L)).thenReturn(tickets);

        ticketBookingService.getTicketsByUserId(1L);

        verify(ticketBookingRepository).getByUserId(1L);
    }

    @Test
    void getTicketsByUserId_check_return_result() {
        List<Ticket> expected = List.of(new Ticket());
        when(ticketBookingRepository.getByUserId(1L)).thenReturn(expected);

        List<Ticket> actual = ticketBookingService.getTicketsByUserId(1L);

        assertEquals(expected, actual);
    }

    @Test
    void getTicketsByUserId_if_user_doesnt_have_tickets() {
        List<Ticket> expected = Collections.emptyList();
        when(ticketBookingRepository.getByUserId(1L)).thenReturn(expected);

        assertDoesNotThrow(() -> ticketBookingService.getTicketsByUserId(1L));
    }

    @Test
    void getTicket_check_call_repository_method() {
        Ticket ticket = new Ticket();
        when(ticketBookingRepository.getById(1L)).thenReturn(ticket);

        ticketBookingService.getTicket(1L);

        verify(ticketBookingRepository).getById(1L);
    }

    @Test
    void getTicket_when_ticket_not_found() {
        when(ticketBookingRepository.getById(1L)).thenReturn(null);

        assertThrows(TicketNotFoundException.class, () -> ticketBookingService.getTicket(1L));
    }

    @Test
    void success_return_ticket() {
        Ticket ticket = new Ticket();
        when(ticketBookingRepository.getById(1L)).thenReturn(ticket);

        assertDoesNotThrow(() -> ticketBookingService.getTicket(1L));
    }

    @Test
    void getTicket_check_result() {
        Ticket expected = new Ticket(1L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 1L, 100);
        when(ticketBookingRepository.getById(1L)).thenReturn(expected);

        Ticket actual = ticketBookingService.getTicket(1L);

        assertEquals(expected, actual);
    }

    @Test
    void bookTicket_when_ticket_place_quantity_less_than_0() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name",
                -1, LocalDateTime.now(), "hall", 1L, 100);

        assertThrows(ValidationException.class, () -> ticketBookingService.bookTicket(ticket));
    }

    @Test
    void bookTicket_when_ticket_place_quantity_is_0() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name",
                0, LocalDateTime.now(), "hall", 1L, 100);

        assertThrows(ValidationException.class, () -> ticketBookingService.bookTicket(ticket));
    }

    @Test
    void when_bookTickets_more_than_freePlace() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name",
                10, LocalDateTime.now(), "hall", 1L, 100);
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 9, 100);

        when(eventService.getEvent(1L)).thenReturn(event);

        assertThrows(ValidationException.class, () -> ticketBookingService.bookTicket(ticket));
    }

    @Test
    void check_count_commonAmount() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name",
                10, LocalDateTime.now(), "hall", 1L, 0);
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        int expectedAmount = 1000;

        when(eventService.getEvent(1L)).thenReturn(event);

        ticketBookingService.bookTicket(ticket);
        int actualAmount = ticket.getCommonAmount();

        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void bookTicket_check_call_repository_method_to_create() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name",
                10, LocalDateTime.now(), "hall", 1L, 0);
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);

        when(eventService.getEvent(1L)).thenReturn(event);

        ticketBookingService.bookTicket(ticket);

        verify(ticketBookingRepository).createTicked(ticket);
    }

    @Test
    void bookTicket_check_call_changeEventTicket_method() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name",
                10, LocalDateTime.now(), "hall", 1L, 0);
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);

        when(eventService.getEvent(1L)).thenReturn(event);

        ticketBookingService.bookTicket(ticket);

        verify(eventService, times(3)).getEvent(1L);
        verify(eventService).updateEvent(1L, event);
    }

    @Test
    void bookTicket_check_result_changeEventTicket_method() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name",
                10, LocalDateTime.now(), "hall", 1L, 0);
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        int expectedFreePlace = 30;

        when(eventService.getEvent(1L)).thenReturn(event);

        ticketBookingService.bookTicket(ticket);
        int actualFreePlace = event.getFreePlace();

        assertEquals(expectedFreePlace, actualFreePlace);
    }

    @Test
    void success_book_ticket() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name",
                10, LocalDateTime.now(), "hall", 1L, 0);
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);

        when(eventService.getEvent(1L)).thenReturn(event);

        assertDoesNotThrow(() -> ticketBookingService.bookTicket(ticket));
    }

    @Test
    void delete_call_check_method() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 1L, 100);
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);

        when(ticketBookingRepository.getById(1L)).thenReturn(ticket);
        when(eventService.getEvent(1L)).thenReturn(event);

        ticketBookingService.deleteTicket(1L);

        verify(ticketBookingRepository).getById(1L);
    }

    @Test
    void delete_when_ticket_not_found() {
        when(ticketBookingRepository.getById(1L)).thenReturn(null);

        assertThrows(TicketNotFoundException.class, () -> ticketBookingService.deleteTicket(1L));
    }

    @Test
    void delete_call_check_call_repository_method_to_delete() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 1L, 100);
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);

        when(ticketBookingRepository.getById(1L)).thenReturn(ticket);
        when(eventService.getEvent(1L)).thenReturn(event);

        ticketBookingService.deleteTicket(1L);

        verify(ticketBookingRepository).deleteTicket(1L);
    }

    @Test
    void delete_call_check_call_eventService_methods() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 1L, 100);
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);

        when(ticketBookingRepository.getById(1L)).thenReturn(ticket);
        when(eventService.getEvent(1L)).thenReturn(event);

        ticketBookingService.deleteTicket(1L);

        verify(eventService).getEvent(1L);
        verify(eventService).updateEvent(1L, event);
    }

    @Test
    void delete_call_check_result_changeEventTicket() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 1L, 100);
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        int expectedQuantityFreePlaceAfterRefundTicket = 41;

        when(ticketBookingRepository.getById(1L)).thenReturn(ticket);
        when(eventService.getEvent(1L)).thenReturn(event);

        ticketBookingService.deleteTicket(1L);
        int actualQuantityFreePlace = event.getFreePlace();

        assertEquals(expectedQuantityFreePlaceAfterRefundTicket, actualQuantityFreePlace);
    }

    @Test
    void success_delete() {
        Ticket ticket = new Ticket(1L, "owner", 1L, "movie name", 1, LocalDateTime.now(),
                "hall", 1L, 100);
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);

        when(ticketBookingRepository.getById(1L)).thenReturn(ticket);
        when(eventService.getEvent(1L)).thenReturn(event);

        assertDoesNotThrow(() -> ticketBookingService.deleteTicket(1L));
    }

    @Test
    void downloadTicket_when_ticket_not_found() {
        when(ticketBookingRepository.getById(1L)).thenReturn(null);

        assertThrows(TicketNotFoundException.class, () -> ticketBookingService.downloadTicket(1L));
    }

    @Test
    void deleteByEventId_check_call_repository(){
        ticketBookingService.deleteByEventId(1L);

        verify(ticketBookingRepository).deleteTicketByEventId(1L);
    }
}
