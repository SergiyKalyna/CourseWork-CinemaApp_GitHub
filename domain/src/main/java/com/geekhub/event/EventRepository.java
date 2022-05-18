package com.geekhub.event;

import com.geekhub.exception.EventNotFoundException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;

public class EventRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final EventRowMapper eventRowMapper;

    public EventRepository(NamedParameterJdbcTemplate jdbcTemplate, EventRowMapper eventRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventRowMapper = eventRowMapper;
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM event WHERE id= :id",
                new MapSqlParameterSource("id", id));
    }

    public List<Event> getAllEvents() {
        return jdbcTemplate.query("SELECT * FROM event ORDER BY time DESC", eventRowMapper)
                .stream()
                .toList();
    }

    public List<Event> findEventsByMovieId(int id) {
        return jdbcTemplate.query("SELECT * FROM event WHERE movie_id= :movie_id ORDER BY time ASC",
                        new MapSqlParameterSource("movie_id", id), eventRowMapper)
                .stream()
                .toList();
    }

    public Event getEvent(Long id) {
        return jdbcTemplate.query("SELECT * FROM event WHERE id= :id",
                        new MapSqlParameterSource("id", id), eventRowMapper)
                .stream()
                .findAny()
                .orElseThrow(() -> new EventNotFoundException(id));
    }

    public void addEvent(Event event) {
        jdbcTemplate.update("INSERT INTO event (time, movie_id, hall_id, free_places, place_cost) " +
                        "VALUES (:time, :movie_id, :hall_id, :free_places, :place_cost)",
                eventRowMapper.getParametersForCreate(event));
    }

    public void updateEvent(Long id, Event updateEvent) {
        jdbcTemplate.update("UPDATE event SET time= :time,movie_id= :movie_id, hall_id= :hall_id, " +
                        "free_places= :free_places,place_cost= :place_cost WHERE id= :id",
                eventRowMapper.getParametersForUpdate(id, updateEvent));
    }

    public List<Event> getAllByDate(String eventDateTime) {
        String searchDate = eventDateTime.substring(0, eventDateTime.length() - 6);
        return jdbcTemplate.query("SELECT * FROM event WHERE time LIKE '%" + searchDate + "%'", eventRowMapper)
                .stream()
                .toList();
    }

    public List<Event> getAllByDateTime(LocalDateTime eventDateTime) {
        String searchDateTime = eventDateTime.toString();
        return jdbcTemplate.query("SELECT * FROM event WHERE time= :time",
                new MapSqlParameterSource("time", searchDateTime), eventRowMapper)
                .stream()
                .toList();
    }
}
