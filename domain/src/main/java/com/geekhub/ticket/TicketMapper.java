package com.geekhub.ticket;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TicketMapper implements RowMapper<Ticket> {
    @Override
    public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Ticket(
                rs.getLong("id"),
                rs.getString("owner"),
                rs.getLong("user_id"),
                rs.getString("movie_name"),
                rs.getInt("place_quantity"),
                LocalDateTime.parse(rs.getString("time")),
                rs.getString("hall_name"),
                rs.getLong("event_id"),
                rs.getInt("common_amount")
        );
    }

    public MapSqlParameterSource getParametersForCreate(Ticket ticket) {
        return new MapSqlParameterSource(
                "owner", ticket.getOwner())
                .addValue("user_id", ticket.getUserId())
                .addValue("movie_name", ticket.getMovieName())
                .addValue("place_quantity", ticket.getPlaceQuantity())
                .addValue("time", String.valueOf(ticket.getTime()))
                .addValue("hall_name", ticket.getHall())
                .addValue("event_id", ticket.getEventId())
                .addValue("common_amount", ticket.getCommonAmount());
    }
}
