package com.geekhub.movie;

import com.geekhub.exception.MovieNotFoundException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class MovieRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final MovieMapper movieMapper;

    public MovieRepository(NamedParameterJdbcTemplate jdbcTemplate, MovieMapper movieMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.movieMapper = movieMapper;
    }

    public void create(Movie movie) {
        jdbcTemplate.update("INSERT INTO movie (title, description, release, genre, production, actors, image_name, " +
                "trailer, rating) VALUES(:title,:description,:release, :genre, :production, :actors, " +
                ":image_name, :trailer, :rating)", movieMapper.getParametersForCreate(movie));
    }

    public List<Movie> showAll() {
        return jdbcTemplate.query("SELECT * FROM movie ORDER BY release DESC", movieMapper)
                .stream()
                .toList();
    }

    public Movie show(int id) {
        return jdbcTemplate.query("SELECT * FROM movie WHERE id= :id",
                        new MapSqlParameterSource("id", id), movieMapper)
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
                "release= :release, genre= :genre, production= :production, actors= :actors, image_name= :image_name, " +
                "trailer= :trailer, rating= :rating WHERE id= :id", movieMapper.getParametersForUpdate(id, updatedMovie));
    }

    public List<Movie> search(String keyWord) {
        String keyword1 = keyWord.substring(0, 1).toUpperCase() + keyWord.substring(1);

        return jdbcTemplate.query("SELECT * FROM movie WHERE title LIKE '%" + keyWord + "%' OR title LIKE '%" +
                keyword1 + "%' OR title LIKE '%" + keyWord.toUpperCase() + "%' OR title LIKE '%" +
                keyWord.toLowerCase() + "%'", movieMapper)
                .stream()
                .toList();
    }

    public List<Movie> showSortedByGenre(String genre) {
        return jdbcTemplate.query("SELECT * FROM movie WHERE genre= :genre ORDER BY release DESC",
                        new MapSqlParameterSource("genre", genre), movieMapper)
                .stream()
                .toList();
    }

    public List<Movie> showSortedByCountry(String production) {
        return jdbcTemplate.query("SELECT * FROM movie WHERE production= :production ORDER BY release DESC",
                        new MapSqlParameterSource("production", production), movieMapper)
                .stream()
                .toList();
    }

    public List<Movie> showAllLastMovie() {
        return jdbcTemplate.query("SELECT * FROM movie ORDER BY release DESC LIMIT 3", movieMapper)
                .stream()
                .toList();
    }

    public List<Movie> showSortedByRating() {
        return jdbcTemplate.query("SELECT * FROM movie ORDER BY rating DESC", movieMapper)
                .stream()
                .toList();
    }
}
