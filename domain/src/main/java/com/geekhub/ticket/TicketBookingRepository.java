package com.geekhub.ticket;

import com.geekhub.exception.TicketNotFoundException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class TicketBookingRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TicketMapper ticketMapper;

    public TicketBookingRepository(NamedParameterJdbcTemplate jdbcTemplate, TicketMapper ticketMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.ticketMapper = ticketMapper;
    }

    public void deleteTicket(Long id) {
        jdbcTemplate.update("DELETE FROM ticket WHERE id= :id",
                new MapSqlParameterSource("id", id));
    }

    public List<Ticket> getByUserId(Long id) {
        return jdbcTemplate.query("SELECT * FROM ticket WHERE user_id= :id ORDER BY time DESC",
                        new MapSqlParameterSource("id", id), ticketMapper)
                .stream()
                .toList();
    }

    public Ticket getById(Long id) {
        return jdbcTemplate.query("SELECT * FROM ticket WHERE id= :id",
                        new MapSqlParameterSource("id", id), ticketMapper)
                .stream()
                .findAny()
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    public void createTicked(Ticket ticket) {
        jdbcTemplate.update("INSERT INTO ticket (owner,user_id,movie_name,place_quantity,time," +
                "hall_name, event_id, common_amount) VALUES (:owner,:user_id,:movie_name,:place_quantity,:time," +
                ":hall_name, :event_id, :common_amount)", ticketMapper.getParametersForCreate(ticket));
    }

    public void deleteTicketByEventId(Long eventId) {
        jdbcTemplate.update("DELETE FROM ticket WHERE event_id= :event_id",
                new MapSqlParameterSource("event_id", eventId));
    }

    public List<Ticket> getAll() {
        return jdbcTemplate.query("SELECT * FROM ticket ORDER BY time DESC", ticketMapper)
                .stream()
                .toList();
    }
}
