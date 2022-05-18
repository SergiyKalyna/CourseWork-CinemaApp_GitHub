package com.geekhub.movie;

import com.geekhub.models.Genre;
import com.geekhub.models.Production;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class MovieRowMapper implements RowMapper<Movie> {

    @Override
    public Movie mapRow(ResultSet rs, int rowNum) throws SQLException {
       byte[] image = rs.getBytes("image");
        return new Movie(
                rs.getInt("id"),
                rs.getString("title"),
                Genre.valueOf((rs.getString("genre")).toUpperCase(Locale.ROOT)),
                rs.getString("description"),
                LocalDate.parse(rs.getString("release")),
                Production.valueOf((rs.getString("production")).toUpperCase(Locale.ROOT)),
                List.of(rs.getString("actors")),
                Base64.getEncoder().encodeToString(image),
                rs.getString("trailer"),
                rs.getInt("rating"));
    }

    public MapSqlParameterSource getParametersForCreate(Movie movie) {
        return new MapSqlParameterSource(
                "title", movie.getTitle())
                .addValue("description", movie.getDescription())
                .addValue("release", String.valueOf(movie.getRelease()))
                .addValue("genre", String.valueOf(movie.getGenre()))
                .addValue("production", String.valueOf(movie.getCountry()))
                .addValue("actors", movie.getActors().toString())
                .addValue("image", movie.getImage())
                .addValue("trailer", movie.getTrailer())
                .addValue("rating", movie.getAverageRating());
    }

    public MapSqlParameterSource getParametersForUpdate(int id, Movie movie) {
        return new MapSqlParameterSource(
                "id", id)
                .addValue("title", movie.getTitle())
                .addValue("description", movie.getDescription())
                .addValue("release", String.valueOf(movie.getRelease()))
                .addValue("genre", String.valueOf(movie.getGenre()))
                .addValue("production", String.valueOf(movie.getCountry()))
                .addValue("actors", movie.getActors().toString())
                .addValue("image", movie.getImage())
                .addValue("trailer", movie.getTrailer())
                .addValue("rating", movie.getAverageRating());
    }
}
