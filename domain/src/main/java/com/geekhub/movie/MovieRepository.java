package com.geekhub.movie;

import com.geekhub.exception.MovieNotFoundException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class MovieRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final MovieRowMapper movieRowMapper;

    public MovieRepository(NamedParameterJdbcTemplate jdbcTemplate, MovieRowMapper movieRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.movieRowMapper = movieRowMapper;
    }

    public void create(Movie movie) {
        jdbcTemplate.update("INSERT INTO movie (title, description, release, genre, production, actors, image, " +
                "trailer, rating) VALUES(:title,:description,:release, :genre, :production, :actors, " +
                ":image, :trailer, :rating)", movieRowMapper.getParametersForCreate(movie));
    }

    public List<Movie> showAll() {
        return jdbcTemplate.query("SELECT * FROM movie ORDER BY release DESC", movieRowMapper)
                .stream()
                .toList();
    }

    public Movie show(int id) {
        return jdbcTemplate.query("SELECT * FROM movie WHERE id= :id",
                        new MapSqlParameterSource("id", id), movieRowMapper)
                .stream()
                .findAny()
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM movie WHERE id= :id",
                new MapSqlParameterSource("id", id));
    }

    public void update(int id, Movie updatedMovie) {
        jdbcTemplate.update("UPDATE  movie SET title= :title, description= :description," +
                "release= :release, genre= :genre, production= :production, actors= :actors, trailer= :trailer, " +
                "rating= :rating, image= :image WHERE id= :id", movieRowMapper.getParametersForUpdate(id, updatedMovie));
    }

    public List<Movie> search(String keyWord) {
        String keyword1 = keyWord.substring(0, 1).toUpperCase() + keyWord.substring(1);

        return jdbcTemplate.query("SELECT * FROM movie WHERE title LIKE '%" + keyWord + "%' OR title LIKE '%" +
                        keyword1 + "%' OR title LIKE '%" + keyWord.toUpperCase() + "%' OR title LIKE '%" +
                        keyWord.toLowerCase() + "%'", movieRowMapper)
                .stream()
                .toList();
    }

    public List<Movie> showSortedByGenre(String genre) {
        return jdbcTemplate.query("SELECT * FROM movie WHERE genre= :genre ORDER BY release DESC",
                        new MapSqlParameterSource("genre", genre), movieRowMapper)
                .stream()
                .toList();
    }

    public List<Movie> showSortedByCountry(String production) {
        return jdbcTemplate.query("SELECT * FROM movie WHERE production= :production ORDER BY release DESC",
                        new MapSqlParameterSource("production", production), movieRowMapper)
                .stream()
                .toList();
    }

    public List<Movie> showAllLastMovie() {
        return jdbcTemplate.query("SELECT * FROM movie ORDER BY release DESC LIMIT 3", movieRowMapper)
                .stream()
                .toList();
    }

    public List<Movie> showSortedByRating() {
        return jdbcTemplate.query("SELECT * FROM movie ORDER BY rating DESC", movieRowMapper)
                .stream()
                .toList();
    }
}
