package com.geekhub.event;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class EventMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Event(
                rs.getLong("id"),
                LocalDateTime.parse(rs.getString("time")),
                rs.getInt("movie_id"),
                rs.getInt("hall_id"),
                rs.getInt("free_places"),
                rs.getInt("place_cost")
        );
    }

    public MapSqlParameterSource getParametersForCreate(Event event) {
        return new MapSqlParameterSource(
                "time", String.valueOf(event.getTime()))
                .addValue("movie_id", event.getMovieId())
                .addValue("hall_id", event.getCinemaHallId())
                .addValue("free_places", event.getFreePlace())
                .addValue("place_cost", event.getPlaceCost());
    }

    public MapSqlParameterSource getParametersForUpdate(Long id, Event event) {
        return new MapSqlParameterSource(
                "id", id)
                .addValue("time", String.valueOf(event.getTime()))
                .addValue("movie_id", event.getMovieId())
                .addValue("hall_id", event.getCinemaHallId())
                .addValue("free_places", event.getFreePlace())
                .addValue("place_cost", event.getPlaceCost());
    }
}
