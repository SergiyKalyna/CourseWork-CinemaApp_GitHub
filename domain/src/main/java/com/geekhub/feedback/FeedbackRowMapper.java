package com.geekhub.feedback;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class FeedbackRowMapper implements RowMapper<Feedback> {

    @Override
    public Feedback mapRow(ResultSet rs, int rowNum) throws SQLException {
        String time = rs.getString("time");
        return new Feedback(
                rs.getLong("id"),
                LocalDateTime.parse(time.substring(0,time.length()-10)),
                rs.getInt("movie_id"),
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getString("feedback"),
                rs.getInt("score")
        );
    }

    public MapSqlParameterSource getParametersForCreate(Feedback feedback) {
        return new MapSqlParameterSource(
                "time", String.valueOf(feedback.getTime()))
                .addValue("movie_id", feedback.getMovieId())
                .addValue("user_id", feedback.getUserId())
                .addValue("name", feedback.getUserName())
                .addValue("feedback", feedback.getFeedback())
                .addValue("score", feedback.getMovieScore());
    }

    public MapSqlParameterSource getParametersForUpdate(Long id, Feedback feedback) {
        return new MapSqlParameterSource(
                "id", id)
                .addValue("time", String.valueOf(feedback.getTime()))
                .addValue("feedback", feedback.getFeedback())
                .addValue("score", feedback.getMovieScore());
    }
}
