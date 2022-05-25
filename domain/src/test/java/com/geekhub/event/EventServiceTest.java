package com.geekhub.event;

import com.geekhub.cinemahall.CinemaHall;
import com.geekhub.cinemahall.CinemaHallService;
import com.geekhub.exception.CinemaHallNotFoundException;
import com.geekhub.exception.CreateEventException;
import com.geekhub.exception.EventNotFoundException;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    EventRepository eventRepository;

    @Mock
    CinemaHallService cinemaHallService;

    @InjectMocks
    EventService eventService;

    @Test
    void add_when_time_is_null() {
        Event event = new Event(1L, null, 1, 1, 40, 100);

        assertThrows(ValidationException.class, () -> eventService.addEvent(event));
    }

    @Test
    void add_when_hall_is_null() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 8, 40, 100);
        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(null);

        assertThrows(CinemaHallNotFoundException.class, () -> eventService.addEvent(event));
    }

    @Test
    void add_when_place_cost_less_than_0() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, -1);
        CinemaHall cinemaHall = new CinemaHall();
        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);

        assertThrows(ValidationException.class, () -> eventService.addEvent(event));
    }

    @Test
    void add_when_place_cost_is_0() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 0);
        CinemaHall cinemaHall = new CinemaHall();
        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);

        assertThrows(ValidationException.class, () -> eventService.addEvent(event));
    }

    @Test
    void add_when_check_DateTime_return_emptyList() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        List<Event> eventList = Collections.emptyList();
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getAllByDateTime(event.getTime())).thenReturn(eventList);

        assertDoesNotThrow(() -> eventService.addEvent(event));
    }

    @Test
    void add_when_event_with_this_time_isPresent() {
        Event event1 = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        Event event2 = new Event(2L, LocalDateTime.now(), 1, 1, 40, 100);
        List<Event> eventList = List.of(event1, event2);
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event1.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getAllByDateTime(event1.getTime())).thenReturn(eventList);

        assertThrows(CreateEventException.class, () -> eventService.addEvent(event1));
    }

    @Test
    void add_when_event_is_overlap_with_another_event_for_2hoursPlus() {
        Event event1 = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        Event event2 = new Event(2L, LocalDateTime.now().plusHours(2), 1, 1, 40, 100);
        List<Event> eventList = List.of(event1, event2);
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event1.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getAllByDateTime(event1.getTime())).thenReturn(eventList);

        assertThrows(CreateEventException.class, () -> eventService.addEvent(event1));
    }

    @Test
    void add_when_event_is_overlap_with_another_event_for_2hoursMinus() {
        Event event1 = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        Event event2 = new Event(2L, LocalDateTime.now().minusHours(2), 1, 1, 40, 100);
        List<Event> eventList = List.of(event1, event2);
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event1.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getAllByDateTime(event1.getTime())).thenReturn(eventList);

        assertThrows(CreateEventException.class, () -> eventService.addEvent(event1));
    }

    @Test
    void add_call_repository_method() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);

        eventService.addEvent(event);

        verify(eventRepository).addEvent(event);
    }

    @Test
    void when_successful_added_event() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);

        eventService.addEvent(event);

        assertDoesNotThrow(() -> eventService.addEvent(event));
    }

    @Test
    void delete_check_isPresent_event() {
        Event event = new Event();
        when(eventRepository.getEvent(1L)).thenReturn(event);

        eventService.deleteEvent(1L);

        verify(eventRepository).getEvent(1L);
    }

    @Test
    void delete_when_event_not_found() {
        Event event = null;
        when(eventRepository.getEvent(1L)).thenReturn(event);

        assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(1L));
    }

    @Test
    void delete_check_call_method() {
        Event event = new Event();
        when(eventRepository.getEvent(1L)).thenReturn(event);

        eventService.deleteEvent(1L);

        verify(eventRepository).delete(1L);
    }

    @Test
    void getEvent_when_id_less_than_0() {
        assertThrows(EventNotFoundException.class, () -> eventService.getEvent(-1L));
    }

    @Test
    void getEvent_when_id_is_0() {
        assertThrows(EventNotFoundException.class, () -> eventService.getEvent(0L));
    }

    @Test
    void getEvent_when_return_null() {
        when(eventRepository.getEvent(1L)).thenReturn(null);

        assertThrows(EventNotFoundException.class, () -> eventService.getEvent(1L));
    }

    @Test
    void getEvent_check_call_method() {
        Event event = new Event();
        when(eventRepository.getEvent(1L)).thenReturn(event);

        eventService.getEvent(1L);

        verify(eventRepository).getEvent(1L);
    }

    @Test
    void getEvent_check_equals_return_object() {
        Event expected = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        when(eventRepository.getEvent(1L)).thenReturn(expected);

        Event actual = eventService.getEvent(1L);

        assertEquals(expected, actual);
    }

    @Test
    void getEvent_check_that_method_return_not_null() {
        Event expected = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        when(eventRepository.getEvent(1L)).thenReturn(expected);

        Event actual = eventService.getEvent(1L);

        assertNotNull(actual);
    }

    @Test
    void findEventsByMovieId_check_to_call_repository() {
        List<Event> events = new ArrayList<>();
        when(eventRepository.findEventsByMovieId(1)).thenReturn(events);

        eventService.findEventsByMovieId(1);

        verify(eventRepository).findEventsByMovieId(1);
    }

    @Test
    void findEventsByMovieId_repository_shouldnt_return_null() {
        List<Event> events = new ArrayList<>();
        when(eventRepository.findEventsByMovieId(1)).thenReturn(events);

        eventService.findEventsByMovieId(1);

        assertNotNull(events);
    }

    @Test
    void findEventsByMovieId_check_filter_where_event_have_places() {
        Event expected = new Event(1L, LocalDateTime.now(), 1, 1, 10, 100);
        List<Event> events = List.of(expected);
        when(eventRepository.findEventsByMovieId(1)).thenReturn(events);

        List<Event> actual = eventService.findEventsByMovieId(1);

        assertEquals(1, actual.size());
    }

    @Test
    void findEventsByMovieId_filter_should_delete_event_without_free_place() {
        Event expected = new Event(1L, LocalDateTime.now(), 1, 1, 10, 100);
        Event expected1 = new Event(2L, LocalDateTime.now(), 1, 1, 0, 100);
        List<Event> events = List.of(expected, expected1);
        when(eventRepository.findEventsByMovieId(1)).thenReturn(events);

        List<Event> actual = eventService.findEventsByMovieId(1);

        assertEquals(1, actual.size());
    }

    @Test
    void findEventsByMovieId_success_return_list() {
        List<Event> events = new ArrayList<>();
        when(eventRepository.findEventsByMovieId(1)).thenReturn(events);

        assertDoesNotThrow(() -> eventService.findEventsByMovieId(1));
    }

    @Test
    void getAll_check_call_repository() {
        List<Event> events = new ArrayList<>();
        when(eventRepository.getAllEvents()).thenReturn(events);

        eventService.getAll();

        verify(eventRepository).getAllEvents();
    }

    @Test
    void getAll_check_result() {
        List<Event> expected = new ArrayList<>();
        when(eventRepository.getAllEvents()).thenReturn(expected);

        List<Event> actual = eventService.getAll();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_check_result_size() {
        List<Event> expected = new ArrayList<>();
        when(eventRepository.getAllEvents()).thenReturn(expected);

        List<Event> actual = eventService.getAll();

        assertEquals(expected.size(), actual.size());
    }

    @Test
    void sortByHall_check_call_repository() {
        List<Event> expected = new ArrayList<>();
        when(eventRepository.getAllEvents()).thenReturn(expected);

        eventService.sortByHall();

        verify(eventRepository).getAllEvents();
    }

    @Test
    void sortByHall_check_right_sort_by_hallId() {
        Event hallId1 = new Event(1L, LocalDateTime.now(), 1, 1, 10, 100);
        Event hallId2 = new Event(2L, LocalDateTime.now(), 1, 2, 11, 100);
        List<Event> expected = List.of(hallId1, hallId2);
        List<Event> testList = List.of(hallId2, hallId1);

        when(eventRepository.getAllEvents()).thenReturn(testList);

        List<Event> actual = eventService.sortByHall();

        assertEquals(expected, actual);
    }

    @Test
    void sortByHall_when_not_equals() {
        Event hallId1 = new Event(1L, LocalDateTime.now(), 1, 1, 10, 100);
        Event hallId2 = new Event(2L, LocalDateTime.now(), 1, 2, 11, 100);
        List<Event> expected = List.of(hallId2, hallId1);
        List<Event> testList = List.of(hallId2, hallId1);

        when(eventRepository.getAllEvents()).thenReturn(testList);

        List<Event> actual = eventService.sortByHall();

        assertNotEquals(expected, actual);
    }

    @Test
    void filterByHallName_check_call_repository() {
        List<Event> events = new ArrayList<>();
        when(eventRepository.getEventsByHallId(1)).thenReturn(events);

        eventService.filterByHall(1);

        verify(eventRepository).getEventsByHallId(1);
    }

    @Test
    void filterByHallName_check_result() {
        List<Event> expected = new ArrayList<>();
        when(eventRepository.getEventsByHallId(1)).thenReturn(expected);

        List<Event> actual = eventService.filterByHall(1);

        assertEquals(expected, actual);
    }

    @Test
    void filterByHallName_check_result_size() {
        List<Event> expected = new ArrayList<>();
        when(eventRepository.getEventsByHallId(1)).thenReturn(expected);

        List<Event> actual = eventService.filterByHall(1);

        assertEquals(expected.size(), actual.size());
    }

    @Test
    void update_when_time_is_null() {
        Event event = new Event(1L, null, 1, 1, 40, 100);

        assertThrows(ValidationException.class, () -> eventService.updateEvent(1L, event));
    }

    @Test
    void update_when_hall_is_null() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 8, 40, 100);
        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(null);

        assertThrows(CinemaHallNotFoundException.class, () -> eventService.updateEvent(1L, event));
    }

    @Test
    void update_when_place_cost_less_than_0() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, -1);
        CinemaHall cinemaHall = new CinemaHall();
        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);

        assertThrows(ValidationException.class, () -> eventService.updateEvent(1L, event));
    }

    @Test
    void update_when_place_cost_is_0() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 0);
        CinemaHall cinemaHall = new CinemaHall();
        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);

        assertThrows(ValidationException.class, () -> eventService.updateEvent(1L, event));
    }

    @Test
    void update_when_checkDuplicateEventByTime_return_emptyList() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        LocalDateTime eventTime = event.getTime();
        List<Event> eventList = Collections.emptyList();
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getEvent(1L)).thenReturn(event);
        when(eventRepository.getAllByDateTime(eventTime)).thenReturn(eventList);

        assertDoesNotThrow(() -> eventService.updateEvent(1L, event));
    }

    @Test
    void update_when_checkDuplicateEventByTime_return_list_with_size_2() {
        Event event1 = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        Event event2 = new Event(2L, LocalDateTime.now(), 1, 1, 40, 100);
        LocalDateTime eventTime = event1.getTime();
        List<Event> eventList = List.of(event1, event2);
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event1.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getEvent(1L)).thenReturn(event1);
        when(eventRepository.getAllByDateTime(eventTime)).thenReturn(eventList);

        assertThrows(CreateEventException.class, () -> eventService.updateEvent(1L, event1));
    }

    @Test
    void update_when_checkDuplicateEventByTime_return_list_with_size_1_but_with_same_id() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        LocalDateTime eventTime = event.getTime();
        List<Event> eventList = List.of(event);
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getEvent(1L)).thenReturn(event);
        when(eventRepository.getAllByDateTime(eventTime)).thenReturn(eventList);

        assertDoesNotThrow(() -> eventService.updateEvent(1L, event));
    }

    @Test
    void update_check_call_method_getEvent() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getEvent(1L)).thenReturn(event);

        eventService.updateEvent(1L, event);

        verify(eventRepository).getEvent(1L);
    }

    @Test
    void update_when_getEvent_return_null() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getEvent(1L)).thenReturn(null);

        assertThrows(EventNotFoundException.class, () -> eventService.updateEvent(1L, event));
    }

    @Test
    void update_when_getEvent_return_success_object() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getEvent(1L)).thenReturn(event);

        assertDoesNotThrow(() -> eventService.updateEvent(1L, event));
    }

    @Test
    void update_when_event_with_this_time_isPresent() {
        Event event1 = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        Event event2 = new Event(2L, LocalDateTime.now(), 1, 1, 40, 100);
        List<Event> eventList = List.of(event1, event2);
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event1.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getAllByDateTime(event1.getTime())).thenReturn(eventList);
        when(eventRepository.getEvent(1L)).thenReturn(event1);

        assertThrows(CreateEventException.class, () -> eventService.updateEvent(1L, event1));
    }

    @Test
    void update_when_event_overlap_with_thisEvent() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        List<Event> eventList = List.of(event);
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getAllByDateTime(event.getTime())).thenReturn(eventList);
        when(eventRepository.getEvent(1L)).thenReturn(event);

        assertDoesNotThrow(() -> eventService.updateEvent(1L, event));
    }

    @Test
    void update_when_not_overlaps_event() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        List<Event> eventList = Collections.emptyList();
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getAllByDateTime(event.getTime())).thenReturn(eventList);
        when(eventRepository.getEvent(1L)).thenReturn(event);

        assertDoesNotThrow(() -> eventService.updateEvent(1L, event));
    }

    @Test
    void update_when_event_is_overlap_with_another_event_for_2hoursPlus() {
        Event event1 = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        Event event2 = new Event(2L, LocalDateTime.now().plusHours(2), 1, 1, 40, 100);
        List<Event> eventList = List.of(event1, event2);
        CinemaHall cinemaHall = new CinemaHall();

        when(eventRepository.getEvent(1L)).thenReturn(event1);
        when(cinemaHallService.getHall(event1.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getAllByDateTime(event1.getTime())).thenReturn(eventList);

        assertThrows(CreateEventException.class, () -> eventService.updateEvent(1L, event1));
    }

    @Test
    void update_when_event_is_overlap_with_another_event_for_2hoursMinus() {
        Event event1 = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        Event event2 = new Event(2L, LocalDateTime.now().minusHours(2), 1, 1, 40, 100);
        List<Event> eventList = List.of(event1, event2);
        CinemaHall cinemaHall = new CinemaHall();

        when(eventRepository.getEvent(1L)).thenReturn(event1);
        when(cinemaHallService.getHall(event1.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getAllByDateTime(event1.getTime())).thenReturn(eventList);

        assertThrows(CreateEventException.class, () -> eventService.updateEvent(1L, event1));
    }

    @Test
    void update_call_repository_method() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getEvent(1L)).thenReturn(event);

        eventService.updateEvent(1L, event);

        verify(eventRepository).updateEvent(1L, event);
    }

    @Test
    void update_when_successful_check_finish() {
        Event event = new Event(1L, LocalDateTime.now(), 1, 1, 40, 100);
        CinemaHall cinemaHall = new CinemaHall();

        when(cinemaHallService.getHall(event.getCinemaHallId())).thenReturn(cinemaHall);
        when(eventRepository.getEvent(1L)).thenReturn(event);

        assertDoesNotThrow(() -> eventService.updateEvent(1L, event));
    }
}
