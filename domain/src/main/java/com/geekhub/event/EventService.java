package com.geekhub.event;

import com.geekhub.cinemahall.CinemaHallService;
import com.geekhub.exception.CinemaHallNotFoundException;
import com.geekhub.exception.CreateEventException;
import com.geekhub.exception.EventNotFoundException;
import com.geekhub.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final CinemaHallService cinemaHallService;

    public EventService(EventRepository eventRepository, CinemaHallService cinemaHallService) {
        this.eventRepository = eventRepository;
        this.cinemaHallService = cinemaHallService;
    }

    public void deleteEvent(Long id) {
        if (eventRepository.getEvent(id) == null) {
            throw new EventNotFoundException(id);
        }
        logger.info("Was deleted event with id - " + id);
        eventRepository.delete(id);
    }

    public void addEvent(Event event) {
        event.setFreePlace(cinemaHallService.getHallPlaces(event.getCinemaHallId()));
        checkEventData(event);

        if (eventRepository.getAllByDateTime(event.getTime()).size() > 0) {
            throw new CreateEventException("Event with this date and time have already present");
        } else if (!checkEventOverlap(event)) {
            throw new CreateEventException("Time of the event is overlap with other events at this cinema hall");
        } else {
            eventRepository.addEvent(event);
            logger.info("Was added new event");
        }
    }

    public Event getEvent(Long id) {
        Optional<Event> event = Optional.ofNullable(eventRepository.getEvent(id));
        logger.info("Was showed event with id - " + id);

        return event.orElseThrow(() -> new EventNotFoundException(id));
    }

    public List<Event> getAll() {
        logger.info("Showed all events");

        return eventRepository.getAllEvents();
    }

    public void updateEvent(Long id, Event event) {
        checkEventData(event);

        if (eventRepository.getEvent(id) == null) {
            throw new EventNotFoundException(id);
        } else if (!checkDuplicateEventByTime(id, event.getTime())) {
            throw new CreateEventException("Event with this date and time have already present");
        } else if (!checkEventOverlap(event)) {
            throw new CreateEventException("Time of the event is overlap with other events at this cinema hall");
        } else {
            logger.info("Was updated event with id - " + id);
            eventRepository.updateEvent(id, event);
        }
    }

    public List<Event> sortByHall() {
        logger.info("Showed all events sorted by hall");
        return eventRepository.getAllEvents()
                .stream()
                .sorted(Comparator.comparing(Event::getCinemaHallId))
                .collect(Collectors.toList());
    }

    public List<Event> findEventsByMovieId(int id) {
        List<Event> events = eventRepository.findEventsByMovieId(id);
        List<Event> filteredEvents = events.stream().filter(event -> event.getFreePlace() > 1).collect(Collectors.toList());

        logger.info("Showed all events by movie with id -" + id);

        return filteredEvents;
    }

    private boolean checkDuplicateEventByTime(Long id, LocalDateTime eventTime) {
        List<Event> events = eventRepository.getAllByDateTime(eventTime);
        if (events.size() == 0) {
            return true;
        } else if (events.size() == 1 && events.get(0).getId().equals(id)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkEventOverlap(Event event) {
        String eventTime = String.valueOf(event.getTime());
        int newEventHour = Integer.parseInt(eventTime.substring(11, 13));
        int count = getNumberOfRepeats(newEventHour, event.getCinemaHallId(), eventTime, event.getId());

        logger.info("Checkining events date to overlap with another events.");
        return count == 0;
    }

    private int getNumberOfRepeats(int newEventHour, int hallId, String eventTime, Long eventId) {
        int count = 0;
        List<Event> events = eventRepository.getAllByDate(eventTime);
        List<Event> sortByHallEvents = events.stream().filter(event -> event.getCinemaHallId() == hallId).toList();

        for (Event event : sortByHallEvents) {
            int eventFromDbTime = event.getTime().getHour();
            if ((eventFromDbTime - newEventHour) >= 3 || (newEventHour - eventFromDbTime) >= 3 || event.getId().equals(eventId)) {
                continue;
            } else {
                count++;
            }
        }
        return count;
    }

    private void checkEventData(Event event) {
        if (event.getTime() == null) {
            throw new ValidationException("Was not choice a date and time of the event");
        } else if (cinemaHallService.getHall(event.getCinemaHallId()) == null) {
            throw new CinemaHallNotFoundException(event.getCinemaHallId());
        } else if (event.getPlaceCost() <= 0) {
            throw new ValidationException("Wrong input cost of the event place");
        }
    }
}
