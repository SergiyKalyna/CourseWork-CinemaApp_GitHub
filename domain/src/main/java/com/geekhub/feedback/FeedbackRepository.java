package com.geekhub.feedback;

import com.geekhub.exception.FeedbackNotFoundException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class FeedbackRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final FeedbackMapper feedbackMapper;

    public FeedbackRepository(NamedParameterJdbcTemplate jdbcTemplate, FeedbackMapper feedbackMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.feedbackMapper = feedbackMapper;
    }

    public List<Feedback> showAll() {
        return jdbcTemplate.query("SELECT * FROM feedback ORDER BY time DESC", feedbackMapper);
    }

    public List<Feedback> showAllByFilmId(int id) {
        return jdbcTemplate.query("SELECT * FROM feedback WHERE movie_id= :id ORDER BY time DESC",
                        new MapSqlParameterSource("id", id), feedbackMapper)
                .stream()
                .toList();
    }

    public Feedback getById(Long id) {
        return jdbcTemplate.query("SELECT * FROM feedback WHERE id= :id",
                        new MapSqlParameterSource("id", id), feedbackMapper)
                .stream()
                .findAny()
                .orElseThrow(() -> new FeedbackNotFoundException(id));
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM feedback WHERE id= :id",
                new MapSqlParameterSource("id", id));
    }

    public void create(Feedback feedback) {
        jdbcTemplate.update("INSERT INTO feedback (time,movie_id,user_id,name,feedback,score) " +
                        "VALUES (:time,:movie_id,:user_id,:name,:feedback,:score)",
                feedbackMapper.getParametersForCreate(feedback));
    }

    public void update(Long id, Feedback updateFeedback) {
        jdbcTemplate.update("UPDATE feedback SET time= :time, feedback= :feedback, score= :score WHERE id= :id",
                feedbackMapper.getParametersForUpdate(id, updateFeedback));
    }
}


