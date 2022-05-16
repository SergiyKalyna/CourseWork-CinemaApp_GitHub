package com.geekhub.cinemahall;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CinemaHallMapper implements RowMapper<CinemaHall> {
    @Override
    public CinemaHall mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CinemaHall(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("places")
        );
    }

    public MapSqlParameterSource getParametersForAdd(CinemaHall cinemaHall) {
        return new MapSqlParameterSource(
                "name", cinemaHall.getName())
                .addValue("places", cinemaHall.getCapacity());
    }

    public MapSqlParameterSource getParametersForUpdate(int id, CinemaHall cinemaHall) {
        return new MapSqlParameterSource(
                "id", id)
                .addValue("name", cinemaHall.getName())
                .addValue("places", cinemaHall.getCapacity());
    }
}
