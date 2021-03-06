package com.geekhub.cinemahall;

import com.geekhub.exception.CinemaHallNotFoundException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class CinemaHallRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final CinemaHallRowMapper cinemaHallRowMapper;

    public CinemaHallRepository(NamedParameterJdbcTemplate jdbcTemplate, CinemaHallRowMapper cinemaHallRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.cinemaHallRowMapper = cinemaHallRowMapper;
    }

    public CinemaHall getById(int id) {
        return jdbcTemplate.query("SELECT * FROM hall WHERE id= :id",
                        new MapSqlParameterSource("id", id), cinemaHallRowMapper)
                .stream()
                .findAny()
                .orElseThrow(() -> new CinemaHallNotFoundException(id));
    }

    public List<CinemaHall> getAllHalls() {
        return jdbcTemplate.query("SELECT * FROM hall", cinemaHallRowMapper)
                .stream()
                .toList();
    }

    public void update(int id, CinemaHall cinemaHall) {
        jdbcTemplate.update("UPDATE hall SET name= :name, places= :places WHERE id= :id",
                cinemaHallRowMapper.getParametersForUpdate(id, cinemaHall));
    }

    public void add(CinemaHall cinemaHall) {
        jdbcTemplate.update("INSERT INTO hall (name, places) VALUES (:name,:places)",
                cinemaHallRowMapper.getParametersForAdd(cinemaHall));
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM hall WHERE id= :id",
                new MapSqlParameterSource("id", id));
    }

}
